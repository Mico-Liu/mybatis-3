### DTD-定义元素
- 在DTD文档中使用ELEMENT关键字来声明一个XML元素
- 语法：<!ELEMENT 元素名称  使用规则>
-（#PCDATA）指示元素的主题内容只能是普通的文本。
- EMPTY：用于指示元素的主体为空。比如<br/>
- ANY：用于指示元素的主题内容为任意类型。
-（子元素）：指示元素中包含的子元素。
#### 定义子元素及描述它们的关系
- 如果子元素用逗号隔开，必须按照声明顺序去编写XML文档
例如：<!ELEMENT FILE(TITLE, AUTHOR, EMAIL)>
- 如果子元素用"|" 分开，说明任选其一。
例如：<!ELEMENT FILE(TITLE|AUTHOR|EMAIL)>
- 用+、*、? 来表示元素出现的次数。
如果元素后面没有+*?表示必须且只能出现一次。
+：表示至少出现一次，一次或多次。n>=1
*：表示可有可无，零次、一次或多次。n>=0
?：表示可以有也可以无，有的话只能出现一次。零次或一次。n=0|n=1
例如：<!ELEMENT MYTITLE((TITLE*,AUTHOR?,EMAIL))*|COMMIT>

#### DTD属性（ATTLIST）定义
- 语法：
<!ATTLIST 元素名称
    属性名 属性类型  约束
    属性名 属性类型  约束
    ......
>
属性声明举例：
<!ATTLIST 商品
 类别 CDATA  #REQUIRED  必须的
 颜色 CDATA  #IMPLIED       可选的
>
对应的XML为：<商品 类别="服装" 颜色="黄色" />
- ``` CDATA：表示属性的取值为普通的文本字符串 ```
- ``` ID：表示属性的取值不能重复 ```
- ``` #REQUIRED：表示该属性必须出现，即必填项```
- ``` #IMPLIED：表示该属性可有可无。即可选项```
- ```#FIXED：表示该属性的取值为一个固定值。语法：#FIXED "固定值"```


参考文章：https://blog.csdn.net/u013087513/article/details/52745509