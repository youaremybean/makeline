# makeline
version :v1.0
功能：根据协议要求从文本中读取文字信息生成bin文件 
功能介绍：因一个产品的需要，将一些文本中中的字符按照协议的要求组织在文件中，加上一些标识，如长度，校验，字符的属性等，生成一个bin文件，使用的java,技术难点(对大牛来说应该不值一提，对于技术还有待成长的我来说，也想了半天，见谅)：使用一个byte统计字符长度会存在超过127的情况，而java中的byte时signed 的，-128到127，所以加有一段转换，超过127后，存储在java中就是负的，处理后，使用单片机当unsigned读出来就是实际超过127正常的长度，便于对应协议的处理，给大家参考下。
希望和社区的大牛多多交流

