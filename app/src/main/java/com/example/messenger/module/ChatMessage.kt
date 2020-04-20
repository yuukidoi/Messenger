package com.example.messenger.module

//    class ChatMessage(val text: String)// ここの引数がでーたベースに保存するテーブルの列に当たるので、テキストだけでは不十分
class  ChatMessage(val id: String,
                   val text: String,
                   val fromId: String,
                   val toId: String,
                   val timestamp: Long){
    constructor(): this("","","","", -1) // データの初期値を設定
}