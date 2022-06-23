package com.xiamo.pwl.common

class PwlEvent {
    var code:Int = 0   //1. 点击图片
    var info:String = ""

    constructor(code: Int) {
        this.code = code
    }

    constructor(code: Int, info: String) {
        this.code = code
        this.info = info
    }
}