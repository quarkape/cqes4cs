# CQES4CS V1.0.0

![banner](https://user-images.githubusercontent.com/43498577/202853353-1f9b97ee-b7ac-4a55-9fc8-f5c2d2319953.png)


💼  一个基于规则配置的综合素质评价系统，助力高校更方便、更高效的开展学生综合素质评价工作。

:raised_hands: 如果需要帮助请联系Q2446247905

&nbsp;

## 安装配置【推荐】

[点击观看安装配置视频教程](https://www.bilibili.com/video/BV1KG4y1Z7Pd?share_source=copy_web)

&nbsp;

##  镜像

|      |                       GitHub                       |                       Gitee                       |
| :--: | :------------------------------------------------: | :-----------------------------------------------: |
| 前端 |   [cqes4cs](https://github.com/quarkape/cqes4cs)   |   [cqes4cs](https://gitee.com/quarkape/cqes4cs)   |
| 后端 | [cqes4csbe](https://github.com/quarkape/cqes4csbe) | [cqes4csbe](https://gitee.com/quarkape/cqes4csbe) |

&nbsp;

## 环境要求

- JDK，[你可以参考这篇文章安装与配置JDK](https://www.runoob.com/java/java-environment-setup.html)
- MySQL，[你可以参考这篇文章使用phpstudy安装MySQL](https://www.itheima.com/news/20191129/171021.html)
- Redis，[你可以参考这篇文章安装与配置Redis](https://juejin.cn/post/7043684887773052965)
- Node.js，[你可以参考这篇文章安装与配置Node.js](https://www.runoob.com/nodejs/nodejs-install-setup.html)
- maven，[你可以参考这篇文章安装与配置maven并了解在IDEA中的使用](https://blog.csdn.net/qq_32588349/article/details/51461182)

**安装完成后，redis和mysql需要进行配置，具体配置参数请看下面mysql配置和redis配置，保证配置与项目一致**

&nbsp;

## 项目技术框架与主要组件

- 前端：Vue，ElementUI，echarts，axios，nprogress，qs
- 后端：springboot，redis，shiro，poi，mybatis
- 数据库：mysql

&nbsp;

## 详细安装、运行以及部署流程

### 1、前端项目安装、运行与部署

1. 下载项目压缩包至本地解压，或者使用`git clone https://github.com/quarkape/cqes4cs.git`克隆项目
2. 进入项目目录，打开命令窗口，执行`npm cache clear --force`清除缓存
3. 进入项目目录，打开命令窗口，执行`npm install`，会开始下载前端项目所需要的所有依赖，这可能需要一定的时间
4. 进入项目目录，打开命令窗口，执行`npm run serve`，会开始运行前端项目
5. 如果需要部署该项目，你需要对前端项目进行构建：进入项目目录，打开命令窗口，执行`npm run build`，执行完后，项目目录下会生成一个`dist`文件夹，这个文件夹就是构建之后的文件，可以直接部署


### <span id="mysqlconf">2、MySQL数据导入与配置说明</span>

1. 使用`mysql -u root -p`进入mysql

2. 依次执行下面的四条命令：

   ```mysql
   # 创建名为cqes4cs的数据库
   create database cqes4cs;
   # 使用数据库cqes4cs
   use cqes4cs;
   # 设置数据库编码方式
   set names utf8;
   # 下面的source后面是sql文件的位置，例如是G:\Projects\IDEA\cqes4cs\cqes4cs.sql的话就是：
   source "G:\Projects\IDEA\cqes4cs\cqes4cs.sql"
   ```

3. 导入数据后，切换到数据库cqes4cs中，查询users表，看是否有一条数据，如果有的话说明导入成功

4. **如果你使用的是phpstudy+mysql workbench，你可以观看上述[视频教程](https://www.bilibili.com/video/BV1KG4y1Z7Pd?share_source=copy_web)，了解如何使用mysql workbench将数据快捷导入到数据库中**。如果你想要安装使用MySQL WorkBench，[你可以参考这篇文章](https://blog.csdn.net/unauna9739/article/details/124702155)

5. 项目要求的mysql配置可以在：`cqes4cs\src\main\resources\application.yml`配置文件中找到：

   ```yaml
   datasource:
       # 用户名为root
       username: root
       # 密码为root
       password: root
       # 端口为3306，数据库为cqes4cs
       url: jdbc:mysql://127.0.0.1:3306/cqes4cs?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8
       driver-class-name: com.mysql.jdbc.Driver
   ```

   请确保电脑上面的相关配置与项目中的配置保持一致（可以改电脑上的配置，也可以改项目中的配置文件）

6. 安装MySQL之后，需要关闭MySQL的`only_full_group_by`功能，[你可以参考这篇文章关闭MySQL的此项功能](https://www.jianshu.com/p/0e2a676a189c)


### <span id="redisconf">3、Redis配置说明</span>

1. 项目要求的redis配置可以在：`cqes4cs\src\main\resources\application.yml`配置文件中找到：

   ```yaml
   redis:
       # 端口6379
       port: 6379
       # 密码kkty
       password: kkty
       host: 127.0.0.1
   ```

   请确保电脑上面的相关配置与项目中的配置保持一致（可以改电脑上的配置，也可以改项目的配置文件）


### 4、后端项目安装、运行与部署

1. 下载项目压缩包至本地解压，或者使用`git clone https://github.com/quarkape/cqes4csbe.git`克隆项目
2. 用IDEA打开项目，IDEA会自动下载pom.xml文件里面的所有依赖，这可能需要一定的时间
3. 下载完成后，运行项目，没有错误说明运行成功
4. 如果需要部署该项目，你需要对后端项目进行打包，进入项目目录，打开命令窗口，执行`mvn package`对项目进行打包。默认打包为jar包，如果需要更换为war包，请自行搜索相关流程。


### 5、静态资源的部署

1. 找到`src\main\java\club\hue\config\`下的`MvcConfig.java`文件并打开，在这个文件下，找到这一行代码:

   ```java
   registry.addResourceHandler("/files/**").addResourceLocations("file:G:/Projects/Materials/cqes4cs/files/");
   ```

   上述代码用于指定静态资源的位置，必须配置正确。你需要将你后端项目中的files文件夹的路径复制，并替换上面代码中的路径。例如，在你的电脑上，你的后端项目中的files文件夹的路径是：`G:\Projects\IDEA\cqes4cs\files`，那么你需要修改上述代码的后半部分：

   ```Java
   // 你需要注意两点：一是使用正斜杠/，二是路径后面一定要加一个/
   registry.addResourceHandler("/files/**").addResourceLocations("file:G:/Projects/IDEA/cqes4cs/files/");
   ```
   

2. files文件夹下面有两个文件夹是空的，上传到github和gitee上面的时候这两个文件夹没有上传上去，但是要正常使用系统的话必须要这两个文件夹才行，需要你自己根据下面的静态资源结构说明去把`configs`和`material`文件夹加上，这两个文件夹分别是存储导出的配置文件和存储学分申请的图片的两个文件夹。

### 6、进入系统与初步配置使用

1. 浏览器地址栏输入`http://localhost:8080`回车进入系统登录页面
2. 登录管理员账号，用户名和密码均为`admin`
3. 登陆后新建一个教师账号，建议新建一个管理年级为2021的账号，这有助于后续与项目提供的测试数据关联
4. 点击右上角头像，退出管理员账号，登录上一步创建的教师账号
5. 在教师端，点击学生管理，点击上传学生名单，选择后端项目中`files\students\test_students_data.xlsx`文件上传，用于生成学生数据
6. 在教师端，点击学分评价，点击上传学生成绩，选择后端项目中`files\grades\test_grades_data.xlsx`文件上传，用于生成学生成绩数据
7. 退出教师账号，登录学生账号，学生账号是可以选择纲刚上传的学生名单文件中的任一学号，初始密码也为学号。一个可用的学号和密码为“51214108037”
8. 在学生端，可以提交学分申请等。至此，基本工作已经差不多完成

&nbsp;

## 静态资源结构说明

```

├─files
    ├─configs  (导出的配置文件的存储位置)
    │      2bb65d3b-11c0-4073-8a68-99f18dcab1c3.xls
    │      
    ├─exports  （导出的综评结果文件的存储位置）
    │      06c4156c-9939-4d11-b93b-2af496c27d43.xls
    │      
    ├─grades  （上传的学生成绩名单的存储位置）
    │      1660235470069.xlsx
    │      
    ├─material  （学分申请的图片存储位置）
    │      166884104794251214108037.png
    │      
    ├─source
    │      efile.pdf  （综合素质评价文件位置）
    │      grade_example.xlsx  （学生成绩模板文件）
    │      student_list_example.xlsx  （学生名单模板文件）
    │      
    └─students
            1660232101630.xls  （上传的学生名单文件存储位置）
```

&nbsp;

## 特色功能

- 提前配置加分规则，减少繁琐的计分过程，提高科学性和效率
- 支持文件导入导出，覆盖常见功能
- 灵活构建评价指标体系，适合不同高校不同专业的培养目标
