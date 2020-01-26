# AutoCommenter
A java program to add code to java source code "automatically"
### Automatically add comments to Java source code

This program can not understand your code. What it is doing is to add some comments based on specific rule and make your program full of comments to meet the requirement of dummy comments required in some courses.

And actually, this program can only add comments to some specific area. For some other areas, it could detect it and send a window to ask you for comments and provide some suggestion. 

<!-- more -->

**Github Page:** https://github.com/sjiang97/AutoCommenter

**Compiled version:** https://github.com/sjiang97/AutoCommenter/releases

**Code version:** https://github.com/sjiang97/AutoCommenter/archive/0.1.zip

#### Sample result:

**Before adding:**

![](https://raw.githubusercontent.com/sjiang97/sjiang97.github.io/master/2019/projects/AutoCommenter/1.png)

**After adding**

![](https://raw.githubusercontent.com/sjiang97/sjiang97.github.io/master/2019/projects/AutoCommenter/2.png)


#### Usage

##### Creating configuration file and put it to the running directory

A model of configuration file as shown below:

```txt
-----special:-----
BAIDU_APP_ID: 
BAIDU_SECURITY_KEY: 
IFLY_APP_ID: 
lineLimit:90
-----conditions:-----
if\s*\(.*\) Following code will be generated if ###.*### met.
while\s*\(.*\) Following code will be excuted for multiple time till ###.*### doesn't met.
do\s*\{ A do...while... loop will be handled.
[^><!=]+={1}.* Assign values.
for\s+\(.*\) Running iteration on required object.
else\s*\{ Do the following if no condition above is met.
return.*; Return ###.*### to the program call from.
break; Break current loop.
System\.out\.println\(.*\) Print information of ###.*### to the user screen.
default\s*\: When default case get touched.
case.*\: Under the case of ###.*###
\.push\( push element to the stack, to the top of it
\.pop\( pop element from the stack, from the top of it
\.peek\( peek element from the stack, never removes data
\.isEmpty\( check if it is empty or not
```

``-----special:-----`` and ``-----conditions:-----`` are field tags, These field tags should matches exactly as what described above. 

###### Special field:

- BAIDU_APP_ID/BAIDU_SECURITY_KEY: Need register Baidu translation API at [here](http://api.fanyi.baidu.com/api/trans/product/index). No need to register if you don't plan to use translation function.
- IFLY_APP_ID: Need register with Ifly at [here](https://www.xfyun.cn/). No need to register if you don't plan to use voice dedication function 
- lineLimit: Words will be cut of at the exact limitation position if it is out of limits.

###### Conditions:

The conditions used to detect on each line of code and make comments at the end of each line. It uses the regular expression of java to detect words. The format of writing condition is ``[Expression] <Words>``. They are separated using space.

``[Expression]``: The java regular expression. If space is needed, use \s instead of an empty place.

``<Words>``: The words to comment at the end of sentences that contains this expression. Use ``###.*###`` to place contents matches ``.*`` on the left hand side.  Only the first one on the left hand side will be placed on the right side.  And code only support  ``###.*###`` syntax right now.

For example, If there is a line in java code: ``System.out.println("Hello World!");``. This meet the requirement of ``System\.out\.println\(.*\)`` defined above. So, the comments to be added to the end of this line is ``Print information of "Hello World!" to the user screen.`` In most case, this will make sense. 

 ###### Other places:

For the other place, including method header, method implementation method, return value description, pass in parameter and some other things, program will promote a window like this to let you input word you want. Default in graph below will be replaced with some guidance words.

![](https://raw.githubusercontent.com/sjiang97/sjiang97.github.io/master/2019/projects/AutoCommenter/3.png)

For some method, such as getter, setter, constructor, program will place some predefined words at here. You could edit it as you need. For the edit method, you could use start listen button to speech language you choose in the first step and use voice dedicator. 



#### Compile & run

##### Download from release page or compile from source.

###### Download from release page:

1. Choose correct library to download and download AutoCommenter from release page
2. ``java -jar AutoCommenter.jar [-d] < Directory or files path >``
3. ``-d`` is optional, It means a directory. If -d exist, program will loop through all files in the path and make comments on them. Only support for one directory. Like, ``java -jar AutoCommenter.jar E:\test``
4. ``<File path>`` could be a list of files separated with space. Like, ``java -jar AutoCommenter.jar file1.java file2.java``.

###### Download from release page:

1. ``git clone https://github.com/sjiang97/AutoCommenter.git``
2. ``find -name "*.java" > sources.txt`` : Find all java files to compile
3. ``mkdir bin``
4. ``javac -d ./bin -cp ./\* @sources.txt``
5. ``cd bin``
6. ``java -classpath ./:../json-jena-1.0.jar:../Msc.jar Test [-d] < Directory or files path >``
7. ``-d`` is optional, It means a directory. If -d exist, program will loop through all files in the path and make comments on them. Only support for one directory. Like, ``java -jar AutoCommenter.jar E:\test``
8. ``<File path>`` could be a list of files separated with space. Like, ``java -jar AutoCommenter.jar file1.java file2.java``.

#### Special notes:

**It requires oracle java 1.8(openjdk on linux) or above**

**It requires a well formated java code:**, In eclipse, kick ``Ctrl+Shift+f`` to make code well formated. And define property of class, fields specifically, i.e. include ``public`` instead of omit it.

It works perfectly on windows machine. It might have problem on mac/linux. 



#### Further

- This is not perfect. Lots of places need to improve
- Didn't add class header automatically
- Program should able to do multiple replacement
- Program should allow user to define mapping relationship between name of method and javadoc sample. 
