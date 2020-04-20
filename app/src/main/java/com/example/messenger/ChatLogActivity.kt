package com.example.messenger

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.messenger.module.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_form_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.sql.Timestamp

class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chatlog.adapter = adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = user.username

//        setupDummyData()

        listenForMessages()

        send_button_chatlog.setOnClickListener{
            Log.d(TAG, "Attempt to message")
            performSendMessage()
        }
    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if(chatMessage != null){
                        Log.d(TAG, chatMessage?.text)

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatItem(chatMessage.text))
                    }else{
                        adapter.add(ChatToItem(chatMessage.text))

                    }



                    }

            }
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }




    private fun performSendMessage(){
        val text = editText_chatlog.text.toString()

        val fromId = FirebaseAuth.getInstance().uid

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if(fromId == null)return

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()//Firebaseにデータベースを作成？後で調べる

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis()/1000 )
        //referencekey はnullであることがないはずなので！！でnullでなう￥いことを示す。fromIdはif(fromId==null)returnでnullを回避している


        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "saved chat message")
            }
    }


    private fun setupDummyData(){
        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatToItem("text to"))
        adapter.add(ChatItem("text from"))
        adapter.add(ChatToItem("Text toto"))


        recyclerview_chatlog.adapter = adapter
    }
}


//自分がわのテキスト
class ChatItem(val text:String): Item<GroupieViewHolder>(){ //引数のtextに表示する文字を入力
    override fun getLayout(): Int {
        return R.layout.chat_form_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text= text //表示するメッセージをセット

    }

}

//相手側のテキスト表示
class ChatToItem(val text: String): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text

    }

}
