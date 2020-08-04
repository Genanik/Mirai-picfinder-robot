package io.genanik.picfinder.plugins.messageReapeater

import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText

var hasBeenProcessed = false

fun MessageChainBuilder.processText(text: PlainText?) {
    if (text == null){
        return
    }
    val src = text.toString()
    val symbolRaw = arrayOf('[', ']', '(', ')', '（', '）', '{', '}', '【', '】', '「', '」', '“', '”', '/', '\\', '‘', '’', '<', '>', '《', '》')
    val symbolNew = arrayOf(']', '[', ')', '(', '）', '（', '}', '{', '】', '【', '」', '「', '”', '“', '\\', '/', '’', '‘', '>', '<', '》', '《')

    //1.得到代码点数量，也即是实际字符数，注意和length()的区别
    //举例：
    //一个emoji表情是一个字符，codePointCount()是1，length()是2。
    val cpCount = src.codePointCount(0, src.length)

    //2.得到字符串的第一个代码点index，和最后一个代码点index
    //举例：比如3个emoji表情，那么它的cpCount=3；firCodeIndex=0；lstCodeIndex=4
    //因为每个emoji表情length()是2，所以第一个是0-1，第二个是2-3，第三个是4-5
    val firCodeIndex = src.offsetByCodePoints(0, 0)
    val lstCodeIndex = src.offsetByCodePoints(0, cpCount - 1)
    var result = ""
    var index = firCodeIndex
    while (index <= lstCodeIndex) {
        //3.获得代码点，判断是否是emoji表情
        //注意，codePointAt(int) 这个int对应的是codeIndex
        //举例:3个emoji表情，取第3个emoji表情，index应该是4
        val codepoint = src.codePointAt(index)
        result = if (isEmojiCharacter(codepoint)) {
            // 特殊字符
            val length = if (Character.isSupplementaryCodePoint(codepoint)) 2 else 1
            src.substring(index, index + length) + result
        } else {
            // 普通字符
            val rawChar = codepoint.toChar()
            val symbolInt = symbolRaw.findOrNull(rawChar)
            if (symbolInt != null) {
                symbolNew[symbolInt] + result
            }else{
                rawChar + result
            }
        }
        //4.确定指定字符（Unicode代码点）是否在增补字符范围内。
        //因为除了表情，还有些特殊字符也是在增补字符方位内的。
        index += if (Character.isSupplementaryCodePoint(codepoint)) 2 else 1
    }
    this.add(result)

    hasBeenProcessed = true
}