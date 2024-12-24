package io.github.zimoyin.qqbot.bot.message

import java.io.Serializable

enum class EmojiType(val type: Int, val id: Int, val description: String) : Serializable {
    NULL(-1, -1, "暂无该表情的枚举"),

    // 表情类型1
    DEYI(1, 4, "得意"),
    LIULEI(1, 5, "流泪"),
    SHUI_T1(1, 8, "睡"),
    DAKU(1, 9, "大哭"),
    GANGA(1, 10, "尴尬"),
    TIAOPI(1, 12, "调皮"),
    WEIXIAO(1, 14, "微笑"),
    KU(1, 16, "酷"),
    KEAI(1, 21, "可爱"),
    AOAN(1, 23, "傲慢"),
    JIE(1, 24, "饥饿"),
    KUN(1, 25, "困"),
    JINGKONG(1, 26, "惊恐"),
    LIUSWEAT(1, 27, "流汗"),
    HANXIAO(1, 28, "憨笑"),
    YOUXIAN(1, 29, "悠闲"),
    FENDOU(1, 30, "奋斗"),
    YIYU(1, 32, "疑问"),
    XU(1, 33, "嘘"),
    YUN(1, 34, "晕"),
    QIAODA(1, 38, "敲打"),
    ZAIJIAN(1, 39, "再见"),
    FADOU(1, 41, "发抖"),
    AIQING(1, 42, "爱情"),
    TIAOTIAO(1, 43, "跳跳"),
    YONGBAO(1, 49, "拥抱"),
    DANOG(1, 53, "蛋糕"),
    COFFEE(1, 60, "咖啡"),
    MEIGUI(1, 63, "玫瑰"),
    AIXIN(1, 66, "爱心"),
    TAIYANG(1, 74, "太阳"),
    YUELIANG(1, 75, "月亮"),
    ZAN(1, 76, "赞"),
    WOSOU(1, 78, "握手"),
    SHENGLI(1, 79, "胜利"),
    FEIKISS(1, 85, "飞吻"),
    XIGUA(1, 89, "西瓜"),
    LENGHAN(1, 96, "冷汗"),
    CAHAN(1, 97, "擦汗"),
    KENBI(1, 98, "抠鼻"),
    GUZHANG(1, 99, "鼓掌"),
    QIUDALE(1, 100, "糗大了"),
    HUAIXIAO(1, 101, "坏笑"),
    ZUOHENGHENG(1, 102, "左哼哼"),
    YOUHENGHENG(1, 103, "右哼哼"),
    HAQIAN(1, 104, "哈欠"),
    WEIQU(1, 106, "委屈"),
    ZUOQINQIN(1, 109, "左亲亲"),
    KELEN(1, 111, "可怜"),
    SHIAI(1, 116, "示爱"),
    BAOQUAN(1, 118, "抱拳"),
    QUANTOU(1, 120, "拳头"),
    AINI(1, 122, "爱你"),
    NO(1, 123, "NO"),
    OK(1, 124, "OK"),
    ZHUANQUAN(1, 125, "转圈"),
    HI(1, 129, "挥手"),
    FIRECRACKERS(1, 137, "鞭炮"),
    HECAI(1, 144, "喝彩"),
    BANGBANGL(1, 147, "棒棒糖"),
    HAND_GUN(1, 169, "手枪"),
    TEA(1, 171, "茶"),
    LEIBEN(1, 173, "泪奔"),
    WUNAI(1, 174, "无奈"),
    MAIMENG(1, 175, "卖萌"),
    XIAOJIUJIE(1, 176, "小纠结"),
    DOGE(1, 179, "doge"),
    JINGXI(1, 180, "惊喜"),
    SAORAO(1, 181, "骚扰"),
    XIAOKU(1, 182, "笑哭"),
    WOZUIMEI(1, 183, "我最美"),
    GHOST(1, 187, "幽灵"),
    DIANZAN(1, 201, "点赞"),
    TUOLIAN(1, 203, "托脸"),
    TUOSAI(1, 212, "托腮"),
    TABLE_SLAP(1, 226, "拍桌"),
    BOBO(1, 214, "啵啵"),
    CENG(1, 219, "蹭一蹭"),
    BAIBAI(1, 222, "抱抱"),
    PAISHOU(1, 227, "拍手"),
    FOXI(1, 232, "佛系"),
    PENLIAN(1, 240, "喷脸"),
    SHUAITOU(1, 243, "甩头"),
    JIAYOUBAOBAO(1, 246, "加油抱抱"),
    NAOKUOTENG(1, 262, "脑阔疼"),
    WULIAN(1, 264, "捂脸"),
    LAYANJING(1, 265, "辣眼睛"),
    OYO(1, 266, "哦哟"),
    TOU(1, 267, "头秃"),
    WENHAO(1, 268, "问号脸"),
    ANZHONG(1, 269, "暗中观察"),
    EMM(1, 270, "emm"),
    CHIGUA(1, 271, "吃瓜"),
    HEHEDA(1, 272, "呵呵哒"),
    WOSUAN(1, 273, "我酸了"),
    WANGWANG(1, 277, "汪汪"),
    HAN_T1(1, 278, "汗"),
    WUYANXIAO(1, 281, "无眼笑"),
    JINGLI(1, 282, "敬礼"),
    MIANWUBIAOQING(1, 284, "面无表情"),
    MOYU(1, 285, "摸鱼"),
    OK_O(1, 287, "哦"),
    ZHENGYAN(1, 289, "睁眼"),
    QIAOKAIXIN(1, 290, "敲开心"),
    MOJINLI(1, 293, "摸锦鲤"),
    QIDAI(1, 294, "期待"),
    BAIXIE(1, 297, "拜谢"),
    YUANBAO(1, 298, "元宝"),
    NIUA(1, 299, "牛啊"),
    YOUQINQIN(1, 305, "右亲亲"),
    NIUQICHONGTIAN(1, 306, "牛气冲天"),
    MIAOMIAO(1, 307, "喵喵"),
    CALL(1, 311, "打Call"),
    SHAPE_SHIFT(1, 312, "变形"),
    ZIXIFENXI(1, 314, "仔细分析"),
    JIAYOU(1, 315, "加油"),
    CLOWN_DOG(1, 317, "菜狗"),
    CHONGBAI(1, 318, "崇拜"),
    BIXIN(1, 319, "比心"),
    QINGZHU(1, 320, "庆祝"),
    JUJUE(1, 322, "拒绝"),
    CHITANG(1, 324, "吃糖"),
    TERRIFIED(1, 325, "惊吓"),
    SHENGQI(1, 326, "生气"),
    FIREWORKS(1, 333, "烟花"),
    FLOWER_FACE(1, 337, "花朵脸"),
    I_OPENED_MY_MIND(1, 338, "我想开了"),
    UNDERSTANDING_REACHED(1, 339, "舔屏"),
    GREETING_NOD(1, 341, "打招呼"),
    SOUR_Q(1, 342, "酸Q"),
    IM_PERPLEXED(1, 343, "我方了"),
    BIG_INNOCENT_VICTIM(1, 344, "大冤种"),
    RED_ENVELOPE_ABUNDANCE(1, 345, "红包多多"),
    YOU_ARE_AWESOME(1, 346, "阴阳_你真棒"),
    NO_TEARS(1, 349, "我没哭真的"),
    HUGGING(1, 350, "贴贴"),
    HEAD_TAPPING(1, 351, "敲头"),


    // 表情类型2
    QINGTIAN(2, 9728, "☀"), // 晴天
    COFFEE2(2, 9749, "☕"), // 咖啡
    KEAI2(2, 9786, "☺"), // 可爱
    SHANGUANG(2, 10024, "✨"), // 闪光
    ERROR(2, 10060, "❌"), // 错误
    WENHAO2(2, 10068, "❔"), // 问号
    MEIGUI2(2, 127801, "🌹"), // 玫瑰
    XIGUA2(2, 127817, "🍉"), // 西瓜
    PINGGUO(2, 127822, "🍎"), // 苹果
    CAOMEI(2, 127827, "🍓"), // 草莓
    LAMIAN(2, 127836, "🍜"), // 拉面
    MIANBAO(2, 127838, "🍞"), // 面包
    BAOBING(2, 127847, "🍧"), // 刨冰
    PIJIU(2, 127866, "🍺"), // 啤酒
    GANBEI(2, 127867, "🍻"), // 干杯
    QINGZHU2(2, 127881, "🎉"), // 庆祝
    CHONG(2, 128027, "🐛"), // 虫
    NIU(2, 128046, "🐮"), // 牛
    JINGYU(2, 128051, "🐳"), // 鲸鱼
    HOUZI(2, 128053, "🐵"), // 猴
    QIANTOU(2, 128074, "👊"), // 拳头
    HAODE(2, 128076, "👌"), // 好的
    LIHAI(2, 128077, "👍"), // 厉害
    GUZHANG2(2, 128079, "👏"), // 鼓掌
    NEIYI(2, 128089, "👙"), // 内衣
    NANHAI(2, 128102, "👦"), // 男孩
    BABA(2, 128104, "👨"), // 爸爸
    AIXIN2(2, 128147, "💓"), // 爱心
    LIWU(2, 128157, "💝"), // 礼物
    SHUIJIAO(2, 128164, "💤"), // 睡觉
    SHUI_T2(2, 128166, "💦"), // 水
    CHUIQI(2, 128168, "💨"), // 吹气
    JIRU(2, 128170, "💪"), // 肌肉
    YOUXIANG(2, 128235, "📫"), // 邮箱
    HUO(2, 128293, "🔥"), // 火
    CIGA(2, 128513, "😁"), // 呲牙
    JIDONG(2, 128514, "😂"), // 激动
    GAOXING(2, 128516, "😄"), // 高兴
    HEIHEI(2, 128522, "😊"), // 嘿嘿
    XIAOSE(2, 128524, "😌"), // 羞涩
    HENGHENG(2, 128527, "😏"), // 哼哼
    BUXIE(2, 128530, "😒"), // 不屑
    HAN_T2(2, 128531, "😓"), // 汗
    SHILOU(2, 128532, "😔"), // 失落
    FEIWEN(2, 128536, "😘"), // 飞吻
    QINQIN(2, 128538, "😚"), // 亲亲
    TIAOQI(2, 128540, "😜"), // 淘气
    TUTOU(2, 128541, "😝"), // 吐舌
    DAKU2(2, 128557, "😭"), // 大哭
    JINZHANG(2, 128560, "😰"), // 紧张
    DENGYAN(2, 128563, "😳"); // 瞪眼

    override fun toString(): String {
        return "EmojiType.$name(type=$type, id=$id, description='$description')"
    }

    companion object {
        @JvmStatic
        fun fromValue(value: Int): EmojiType? {
            for (emojiType in entries) {
                if (emojiType.id == value) {
                    return emojiType
                }
            }
            return null
        }

        @JvmStatic
        fun fromValueID(value: String): EmojiType? {
            try {
                val id = value.toInt()
                for (emojiType in entries) {
                    if (emojiType.id == id) {
                        return emojiType
                    }
                }
            } catch (e: Exception) {
                return null
            }
            return null
        }
    }
}
