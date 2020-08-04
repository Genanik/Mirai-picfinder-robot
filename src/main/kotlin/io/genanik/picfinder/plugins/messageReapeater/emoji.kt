package io.genanik.picfinder.plugins.messageReapeater

fun isEmojiCharacter(codePoint: Int): Boolean {
    return (codePoint in 0x2600..0x27BF // 杂项符号与符号字体
            || codePoint == 0x303D || codePoint == 0x2049 || codePoint == 0x203C || codePoint in 0x2000..0x200F //
            || codePoint in 0x2028..0x202F //
            || codePoint == 0x205F //
            || codePoint in 0x2065..0x206F //
            /* 标点符号占用区域 */
            || codePoint in 0x2100..0x214F // 字母符号
            || codePoint in 0x2300..0x23FF // 各种技术符号
            || codePoint in 0x2B00..0x2BFF // 箭头A
            || codePoint in 0x2900..0x297F // 箭头B
            || codePoint in 0x3200..0x32FF // 中文符号
            || codePoint in 0xD800..0xDFFF // 高低位替代符保留区域
            || codePoint in 0xE000..0xF8FF // 私有保留区域
            || codePoint in 0xFE00..0xFE0F // 变异选择器
            || codePoint >= 0x10000) // Plane在第二平面以上的，char都不可以存，全部都转
}