SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` bigint(200) NOT NULL AUTO_INCREMENT,
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章内容',
  `summary` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章摘要',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '所属分类id',
  `thumbnail` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '缩略图',
  `is_top` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '是否置顶（0否，1是）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '状态（0已发布，1草稿）',
  `view_count` bigint(200) NULL DEFAULT 0 COMMENT '访问量',
  `is_comment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '是否允许评论 1是，0否',
  `create_by` bigint(20) NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` bigint(20) NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文章表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (1, 'Spring Boot文件上传功能的实现', '## 1. Spring MVC处理文件上传\r\n\r\n* 一旦有请求被DispatcherServlet类处理，DispatcherServlet类会先调用MultipartResolver实现类中的isMultipart()方法判断该请求是不是文件上传请求。\r\n\r\n* 如果是文件上传请求，DispatcherServlet类会调用MultipartResolver实现类中的resolveMultipart()方法重新封装该请求对象，并返回一个新的MultipartHttpServletRequest对象供后续处理流程使用。\r\n\r\n\r\n\r\n> **文件上传请求的结构**\r\n\r\n1. 请求头：与常规HTTP请求一样，包括请求方法、URL、HTTP版本等信息。\r\n2. 分隔符：在请求头的Content-Type字段中还会指定一个分隔符（boundary），用于分隔请求体中的不同部分。分隔符必须在每个部分的开头和结尾处都出现，并且不能与任何部分的内容重复。\r\n3. 每个部分：每个部分都由一个部分头和部分体组成，部分头包含了该部分的信息，如Content-Disposition、Content-Type等，部分体则是该部分的内容。\r\n\r\n```http\r\nPOST /uploadFile HTTP/1.1\r\nHost: localhost:8080\r\nContent-Type: multipart/form-data; boundary=----WebKitFormBoundaryjKJjwy7WfTShlnxw\r\n\r\n------WebKitFormBoundaryjKJjwy7WfTShlnxw\r\nContent-Disposition: form-data; name=\"user\"\r\n\r\n小明\r\n------WebKitFormBoundaryjKJjwy7WfTShlnxw\r\nContent-Disposition: form-data; name=\"file1\"; filename=\"example1.txt\"\r\nContent-Type: text/plain\r\n\r\nThis is the contents of the file1.\r\n\r\n------WebKitFormBoundaryjKJjwy7WfTShlnxw\r\nContent-Disposition: form-data; name=\"file2\"; filename=\"example2.png\"\r\nContent-Type: image/png\r\n\r\nThis is the contents of the file2.\r\n\r\n------WebKitFormBoundaryjKJjwy7WfTShlnxw--\r\n```\r\n\r\n\r\n\r\n## 2. Spring Boot文件上传功能的实现\r\n\r\n> **Spring Boot文件上传配置项**\r\n\r\n由于Spring Boot自动配置机制的存在，只要在pom.xml文件中引入spring-boot-starter-web依赖即可直接调用文件上传功能。开发人员在文件上传时也可能有一些特殊的需求，有如下配置项可以进行修改：\r\n\r\n**spring.servlet.multipart.enabled**：是否支持multipart上传文件，默认true。\r\n**spring.servlet.multipart.file-size-threshold**：文件大小阈值，当大于这个阈值时将写入磁盘，否则存在内存中，（默认值0，一般情况下不用特意修改）。\r\n**spring.servlet.multipart.location**：上传文件的临时目录。\r\n<span style=\"border: 1px solid; border-color: red;\">**spring.servlet.multipart.max-file-size**</span>：最大支持的文件大小，默认1MB，该值可适当调整。\r\n<span style=\"border: 1px solid; border-color: red;\">**spring.servlet.multipart.max-request-size**</span>：最大支持的请求大小，默认10MB。\r\n**spring.servlet.multipart.resolve-lazily**：判断是否要延迟解析文件，默认false（相当于懒加载，一般情况下不用特意修改）。\r\n\r\n\r\n\r\n> **实现案例**\r\n\r\n1. 在static目录下新建 `upload-test.html`\r\n\r\n   ```html\r\n   <!DOCTYPE html>\r\n   <html lang=\"en\">\r\n   <head>\r\n       <meta charset=\"UTF-8\">\r\n       <title>Spring Boot 文件上传测试</title>\r\n   </head>\r\n   <body>\r\n   <form action=\"/uploadFile\" method=\"post\" enctype=\"multipart/form-data\">\r\n       <input type=\"file\" name=\"file\" />\r\n       <input type=\"submit\" value=\"文件上传\" />\r\n   </form>\r\n   </body>\r\n   </html>\r\n   ```\r\n\r\n\r\n2. 在controller包下新建 `UploadController ` 类\r\n\r\n   * 由于Spring Boot已经自动配置了StandardServletMultipartResolver类来处理文件上传请求，因此能够直接在controller方法中使用MultipartFile读取文件信息。 \r\n\r\n   ```java\r\n   @Controller\r\n   public class UploadController {\r\n       // 上传文件的保存路径\r\n       private final static String FILE_UPLOAD_PATH = \"E:\\\\test\\\\upload\\\\\";\r\n   \r\n       @RequestMapping(value = \"/uploadFile\", method = RequestMethod.POST)\r\n       @ResponseBody\r\n       public String uploadFile(MultipartFile file) {\r\n           if (file.isEmpty()) {\r\n               return \"上传失败\";\r\n           }\r\n   \r\n           // 生成文件名，保存文件\r\n           String filename = file.getOriginalFilename();\r\n           DateTimeFormatter formatter = DateTimeFormatter.ofPattern(\"yyyyMMdd_HHmmssSSS_\");\r\n           String newFilename = LocalDateTime.now().format(formatter) + filename;\r\n           try {\r\n               file.transferTo(new File(FILE_UPLOAD_PATH + newFilename));\r\n           } catch (IOException e) {\r\n               e.printStackTrace();\r\n           }\r\n   \r\n           return \"上传成功，地址为：\" + FILE_UPLOAD_PATH + newFilename;\r\n       }\r\n   }   \r\n   ```\r\n\r\n3. 启动Spring Boot项目，打开浏览器并输入测试页面地址：[localhost:8080/upload-test.html](http://localhost:8080/upload-test.html) \r\n\r\n   * 如果文件存储目录还没有创建的话，首先需要创建该目录\r\n   * 单个文件大小超出设定值或请求的大小超出设定值，需要调整文件上传的配置项来避免这种异常信息的产生\r\n\r\n   \r\n\r\n> **Spring Boot文件上传路径回显**\r\n\r\n新建config包并在config包中新建 `MyMvcConfig` 类\r\n\r\n增加一个自定义静态资源映射配置，使得静态资源可以通过该映射地址被访问到：\r\n\r\n* `addResourceHandler()`：用于指定URL路径，即在浏览器中访问的路径\r\n* `addResourceLocations()`：用于指定静态资源路径\r\n\r\n```java\r\n@Configuration\r\npublic class MyMvcConfig implements WebMvcConfigurer {\r\n\r\n    public void addResourceHandlers(ResourceHandlerRegistry registry) {\r\n        registry.addResourceHandler(\"/upload/**\").addResourceLocations(\"file:E:\\\\test\\\\upload\\\\\");\r\n    }\r\n}\r\n```\r\n\r\n\r\n\r\n## 3. Spring Boot多文件上传功能的实现\r\n\r\n> **文件名相同时的多文件上传处理**\r\n\r\n1. 在static目录中新建 `upload-same-file-name.html`\r\n\r\n   ```html\r\n   <!DOCTYPE html>\r\n   <html lang=\"en\">\r\n   <head>\r\n       <meta charset=\"UTF-8\">\r\n       <title>Spring Boot 多文件上传测试（filename相同）</title>\r\n   </head>\r\n   <body>\r\n   <form action=\"/uploadFilesBySameName\" method=\"post\" enctype=\"multipart/form-data\">\r\n       <input type=\"file\" name=\"files\"/><br><br>\r\n       <input type=\"file\" name=\"files\"/><br><br>\r\n       <input type=\"file\" name=\"files\"/><br><br>\r\n       <input type=\"submit\" value=\"文件上传\"/>\r\n   </form>\r\n   </body>\r\n   </html>\r\n   ```\r\n\r\n2. 在 `UploadController` 类中新增 `uploadFilesBySameName()` 方法\r\n\r\n   ```java\r\n   @RequestMapping(value = \"/uploadFilesBySameName\", method = RequestMethod.POST)\r\n   @ResponseBody\r\n   public String uploadFilesBySameName(MultipartFile[] files) {\r\n       if (files == null || files.length == 0) {\r\n           return \"参数异常\";\r\n       }\r\n   \r\n       String uploadResult = \"上传成功，地址为：<br>\";\r\n       for (MultipartFile file : files) {\r\n           if (file.isEmpty()) {\r\n               continue;\r\n           }\r\n           String filename = file.getOriginalFilename();\r\n           DateTimeFormatter formatter = DateTimeFormatter.ofPattern(\"yyyyMMdd_HHmmssSSS_\");\r\n           String newFilename = LocalDateTime.now().format(formatter) + filename;\r\n           try {\r\n               file.transferTo(new File(FILE_UPLOAD_PATH + newFilename));\r\n           } catch (IOException e) {\r\n               e.printStackTrace();\r\n           }\r\n           uploadResult += FILE_UPLOAD_PATH + newFilename + \"<br>\";\r\n       }\r\n       return uploadResult;\r\n   }\r\n   ```\r\n\r\n\r\n\r\n> **文件名不同时的多文件上传处理**\r\n\r\n1. 在static目录中新建 `upload-different-file-name.html`\r\n\r\n   ```html\r\n   <!DOCTYPE html>\r\n   <html lang=\"en\">\r\n   <head>\r\n       <meta charset=\"UTF-8\">\r\n       <title>Spring Boot 多文件上传测试（filename不同）</title>\r\n   </head>\r\n   <body>\r\n   <form action=\"/uploadFilesByDifferentName\" method=\"post\" enctype=\"multipart/form-data\">\r\n       <input type=\"file\" name=\"file1\"/><br><br>\r\n       <input type=\"file\" name=\"file2\"/><br><br>\r\n       <input type=\"file\" name=\"file3\"/><br><br>\r\n       <input type=\"submit\" value=\"文件上传\"/>\r\n   </form>\r\n   </body>\r\n   </html>\r\n   ```\r\n\r\n2. 在 `UploadController` 类中新增 `uploadFilesByDifferentName()` 方法\r\n\r\n   * **HttpServletRequest** 和 **HttpServletResponse**：这是 HTTP 请求和响应的 Java 表示。可以作为参数直接传入方法，使用它们来获取更多关于请求和响应的详细信息，或者执行特定的操作。\r\n\r\n   ```java\r\n   @Autowired\r\n   private StandardServletMultipartResolver multipartResolver;\r\n   \r\n   @RequestMapping(value = \"/uploadFilesByDifferentName\", method = RequestMethod.POST)\r\n   @ResponseBody\r\n   public String uploadFilesByDifferentName(HttpServletRequest request) {\r\n       // 如果不是文件上传请求则不处理\r\n       if (!multipartResolver.isMultipart(request)) {\r\n           return \"请选择文件\";\r\n       }\r\n   \r\n       // 将 HttpServletRequest 对象转换为 MultipartHttpServletRequest 对象，之后读取文件\r\n       MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;\r\n       Iterator<String> iter = multipartRequest.getFileNames();\r\n       List<MultipartFile> files = new ArrayList<>();\r\n       while (iter.hasNext()) {\r\n           MultipartFile file = multipartRequest.getFile(iter.next());\r\n           files.add(file);\r\n       }\r\n   \r\n       if (CollectionUtils.isEmpty(files)) {\r\n           return \"请选择文件\";\r\n       }\r\n   \r\n       String uploadResult = \"上传成功，地址为：<br>\";\r\n       for (MultipartFile file : files) {\r\n           if (file.isEmpty()) {\r\n               continue;\r\n           }\r\n           String filename = file.getOriginalFilename();\r\n           DateTimeFormatter formatter = DateTimeFormatter.ofPattern(\"yyyyMMdd_HHmmssSSS_\");\r\n           String newFilename = LocalDateTime.now().format(formatter) + filename;\r\n           try {\r\n               file.transferTo(new File(FILE_UPLOAD_PATH + newFilename));\r\n           } catch (IOException e) {\r\n               e.printStackTrace();\r\n           }\r\n           uploadResult += FILE_UPLOAD_PATH + newFilename + \"<br>\";\r\n       }\r\n       return uploadResult;\r\n   }\r\n   ```\r\n\r\n\r\n\r\n', '使用Spring Boot实现文件上传及其相关的注意事项', 1, 'https://img1.baidu.com/it/u=4026470308,2412268569&fm=253&fmt=auto&app=138&f=JPEG?w=824&h=500', '1', '0', 106, '0', NULL, '2023-02-23 23:20:11', NULL, NULL, 0);
INSERT INTO `article` VALUES (2, 'Python文件读写', '文件读写操作是编程中最基础也是最实用的技能之一。无论是保存用户数据、处理配置文件，还是分析日志文件，都离不开文件操作。本教程将用通俗易懂的语言，带领初学者掌握Python文件读写的基本技能。\r\n\r\n\r\n\r\n## 1. 什么是文件读写？\r\n\r\n想象一下，文件就像一个储物柜，而Python程序就是管理这个储物柜的管理员。\"读取\"文件就是从储物柜中取出物品查看，\"写入\"文件就是把物品放入储物柜保存。\r\n\r\n\r\n\r\n## 2. 基本文件操作\r\n\r\n### ① 打开文件\r\n\r\n在操作文件前，我们需要先\"打开\"它，就像打开储物柜的门一样。Python使用`open()`函数来完成这个操作。\r\n\r\n```python\r\n# 基本语法：open(\'文件名\', \'操作模式\')\r\nfile = open(\'my_file.txt\', \'r\')\r\n```\r\n\r\n这里的操作模式有几种常见选择：\r\n\r\n- `\'r\'` - 读取模式（Read）：只能查看文件内容，不能修改\r\n- `\'w\'` - 写入模式（Write）：可以写入内容，但会清空原有内容\r\n- `\'a\'` - 追加模式（Append）：可以在文件末尾添加内容，不会清空原有内容\r\n- `\'r+\'` - 读写模式：既可以读取也可以写入\r\n\r\n### ② 关闭文件\r\n\r\n打开文件后，处理完毕一定要记得\"关上储物柜的门\"。\r\n\r\n```python\r\nfile = open(\'my_file.txt\', \'r\')\r\n# 做一些操作...\r\nfile.close()  # 关闭文件\r\n```\r\n\r\n忘记关闭文件可能导致资源泄露或数据丢失，就像离开时没锁门一样存在风险。\r\n\r\n### ③ 使用with语句（强烈推荐）\r\n\r\nPython提供了一种更优雅的方式来确保文件正确关闭 - `with`语句。使用它可以让Python自动处理文件的关闭操作，即使代码出错也能保证文件被正确关闭。\r\n\r\n```python\r\n# with语句会自动关闭文件，不需要手动调用close()\r\nwith open(\'my_file.txt\', \'r\') as file:\r\n    # 在这里处理文件...\r\n    content = file.read()\r\n# 离开with代码块后，文件自动关闭\r\n```\r\n\r\n这就像有一个助手，确保你离开储物室时一定会锁门。对初学者来说，养成使用`with`语句的习惯非常重要。\r\n\r\n\r\n\r\n## 3. 读取文件内容\r\n\r\n当我们打开文件后，可以使用多种方法读取其中的内容。\r\n\r\n### ① 读取全部内容\r\n\r\n```python\r\nwith open(\'my_file.txt\', \'r\', encoding=\'utf-8\') as file:\r\n    content = file.read()  # 读取文件的全部内容\r\n    print(content)\r\n```\r\n\r\n这里的`encoding=\'utf-8\'`告诉Python使用UTF-8编码来理解文件内容，这对于包含中文等非ASCII字符的文件尤其重要。\r\n\r\n### ② 逐行读取\r\n\r\n有时文件很大，或者我们只需要逐行处理，可以使用以下方法：\r\n\r\n```python\r\n# 方法1：使用readlines()获取所有行的列表\r\nwith open(\'my_file.txt\', \'r\') as file:\r\n    lines = file.readlines()  # 返回一个列表，每一行是列表中的一个元素\r\n    for line in lines:\r\n        # strip()可以去除每行末尾的换行符(\\n)\r\n        print(line.strip())  \r\n\r\n# 方法2：直接遍历文件对象（更节省内存）\r\nwith open(\'my_file.txt\', \'r\') as file:\r\n    for line in file:  # 文件对象本身是可迭代的\r\n        print(line.strip())\r\n```\r\n\r\n第二种方法对大文件更友好，因为它不会一次性将整个文件加载到内存中。\r\n\r\n\r\n\r\n## 4. 写入文件内容\r\n\r\n写入文件就是将数据保存到文件中，可能是创建新内容，也可能是覆盖或追加内容。\r\n\r\n### ① 写入字符串\r\n\r\n```python\r\n# 写入模式(\'w\')会覆盖文件中的所有内容\r\nwith open(\'output.txt\', \'w\', encoding=\'utf-8\') as file:\r\n    file.write(\'你好，这是第一行\\n\')  # \\n表示换行\r\n    file.write(\'这是第二行\')\r\n```\r\n\r\n在这个例子中：\r\n\r\n- `\'w\'`模式会创建一个新文件（如果不存在）或清空现有文件的所有内容\r\n- `encoding=\'utf-8\'`确保中文字符能正确写入\r\n- `\\n`是换行符，告诉计算机在这里开始新的一行\r\n\r\n### ② 追加内容\r\n\r\n如果你想保留文件原有内容，并在末尾添加新内容，可以使用追加模式：\r\n\r\n```python\r\nwith open(\'output.txt\', \'a\', encoding=\'utf-8\') as file:\r\n    file.write(\'\\n这是追加的第三行\')  # 先写一个换行符，再写内容\r\n```\r\n\r\n\r\n\r\n## 5. 实用案例：简单的记事本程序\r\n\r\n让我们通过一个小例子来应用所学知识：\r\n\r\n```python\r\ndef simple_notepad():\r\n    print(\"欢迎使用简易记事本！\")\r\n    filename = input(\"请输入要保存的文件名: \")\r\n    \r\n    if not filename.endswith(\'.txt\'):\r\n        filename += \'.txt\'  # 确保文件名有.txt后缀\r\n    \r\n    notes = []\r\n    print(\"请输入笔记内容（输入\'保存\'单独一行来结束）:\")\r\n    \r\n    while True:\r\n        line = input()\r\n        if line == \'保存\':\r\n            break\r\n        notes.append(line)\r\n    \r\n    # 写入文件\r\n    with open(filename, \'w\', encoding=\'utf-8\') as file:\r\n        for note in notes:\r\n            file.write(note + \'\\n\')\r\n    \r\n    print(f\"笔记已保存到 {filename}\")\r\n    \r\n    # 读取并显示文件内容\r\n    print(\"\\n文件内容:\")\r\n    with open(filename, \'r\', encoding=\'utf-8\') as file:\r\n        print(file.read())\r\n\r\n# 运行程序\r\nsimple_notepad()\r\n```\r\n\r\n这个小程序允许用户创建一个文本文件，输入多行内容，然后保存并显示结果。\r\n\r\n\r\n\r\n## 6. 处理CSV文件\r\n\r\nCSV（Comma-Separated Values，逗号分隔值）是一种常见的数据交换格式，可以被Excel等软件打开。Python有专门的库来处理CSV文件。\r\n\r\n```python\r\nimport csv  # 导入csv模块\r\n\r\n# 读取CSV文件\r\nwith open(\'students.csv\', \'r\', newline=\'\', encoding=\'utf-8\') as csvfile:\r\n    reader = csv.reader(csvfile)\r\n    for row in reader:\r\n        print(row)  # row是一个列表，包含该行的所有字段\r\n\r\n# 写入CSV文件\r\nstudents = [\r\n    [\'姓名\', \'年龄\', \'成绩\'],  # 表头\r\n    [\'小明\', \'15\', \'90\'],\r\n    [\'小红\', \'16\', \'95\'],\r\n    [\'小李\', \'15\', \'85\']\r\n]\r\n\r\nwith open(\'new_students.csv\', \'w\', newline=\'\', encoding=\'utf-8\') as csvfile:\r\n    writer = csv.writer(csvfile)\r\n    writer.writerows(students)  # 一次写入多行\r\n```\r\n\r\n这里我们使用了`newline=\'\'`参数来确保写入的CSV文件格式正确，不会出现多余的空行。\r\n\r\n\r\n\r\n## 7. 处理JSON文件\r\n\r\nJSON（JavaScript Object Notation）是一种轻量级的数据交换格式，在网络传输数据时非常常用。Python内置了json模块来处理它。\r\n\r\n```python\r\nimport json  # 导入json模块\r\n\r\n# 创建一个Python字典\r\nstudent = {\r\n    \'name\': \'小明\',\r\n    \'age\': 15,\r\n    \'scores\': {\r\n        \'math\': 90,\r\n        \'english\': 85,\r\n        \'history\': 92\r\n    },\r\n    \'hobbies\': [\'篮球\', \'编程\', \'阅读\']\r\n}\r\n\r\n# 将Python对象写入JSON文件\r\nwith open(\'student.json\', \'w\', encoding=\'utf-8\') as jsonfile:\r\n    # ensure_ascii=False 确保中文字符正确写入\r\n    # indent=4 使得JSON文件有漂亮的缩进格式\r\n    json.dump(student, jsonfile, ensure_ascii=False, indent=4)\r\n\r\n# 从JSON文件读取数据\r\nwith open(\'student.json\', \'r\', encoding=\'utf-8\') as jsonfile:\r\n    loaded_student = json.load(jsonfile)\r\n    print(f\"姓名: {loaded_student[\'name\']}\")\r\n    print(f\"数学成绩: {loaded_student[\'scores\'][\'math\']}\")\r\n```\r\n\r\nJSON格式很适合存储结构化数据，比纯文本文件更容易处理复杂的数据结构。\r\n\r\n\r\n\r\n## 8. 常见问题和解决方案\r\n\r\n### ① FileNotFoundError\r\n\r\n当你尝试打开一个不存在的文件进行读取时，会出现这个错误。\r\n\r\n```python\r\ntry:\r\n    with open(\'不存在的文件.txt\', \'r\') as file:\r\n        content = file.read()\r\nexcept FileNotFoundError:\r\n    print(\"文件不存在，请检查文件名或路径是否正确\")\r\n```\r\n\r\n### ② UnicodeDecodeError\r\n\r\n当Python无法正确解码文件内容时会出现此错误，通常是编码设置不正确导致的。\r\n\r\n```python\r\ntry:\r\n    with open(\'chinese_text.txt\', \'r\', encoding=\'utf-8\') as file:\r\n        content = file.read()\r\nexcept UnicodeDecodeError:\r\n    print(\"文件编码错误，尝试其他编码方式\")\r\n    with open(\'chinese_text.txt\', \'r\', encoding=\'gbk\') as file:\r\n        content = file.read()\r\n```\r\n\r\n### ③ 路径问题\r\n\r\n在不同操作系统中，文件路径的表示方式可能不同。Python提供了os模块来处理这个问题。\r\n\r\n```python\r\nimport os\r\n\r\n# 使用os.path.join()创建路径，适用于所有操作系统\r\ndata_folder = \"数据\"\r\nfilename = \"用户信息.txt\"\r\nfile_path = os.path.join(data_folder, filename)\r\n\r\nwith open(file_path, \'r\', encoding=\'utf-8\') as file:\r\n    content = file.read()\r\n```\r\n\r\n\r\n\r\n## 总结\r\n\r\nPython文件操作的基本步骤是：\r\n\r\n1. 使用`open()`函数打开文件，指定正确的模式和编码\r\n2. 读取(`read()`, `readlines()`)或写入(`write()`, `writelines()`)文件内容\r\n3. 确保文件正确关闭（最好使用`with`语句）\r\n\r\n记住这些基础知识，你已经可以处理大多数文件操作需求了。随着经验的积累，你可以进一步学习更高级的文件处理技术，如处理二进制文件、使用pandas处理大型数据集等。', 'Python读写文本文件学习笔记', 2, 'https://www.runoob.com/wp-content/uploads/2014/05/python3.png', '1', '0', 125, '0', NULL, '2023-03-21 14:58:30', NULL, NULL, 0);
INSERT INTO `article` VALUES (3, 'Pycharm的安装和使用', 'PyCharm是Python开发的专业集成开发环境(IDE)，它提供了代码编写、调试、测试等一系列功能，极大地提高了Python编程的效率。本教程将指导初学者如何安装PyCharm并掌握其基本使用方法。\r\n\r\n\r\n\r\n## 一、PyCharm介绍\r\n\r\n### 什么是PyCharm？\r\n\r\nPyCharm是由JetBrains公司开发的Python IDE，就像Word是写文档的工具，PyCharm是写Python代码的专业工具。它有两个版本：\r\n\r\n- **社区版(Community Edition)**：免费版，提供基本的Python开发功能\r\n- **专业版(Professional Edition)**：付费版，提供更多高级功能，如Web开发支持、数据库工具等\r\n\r\n对于初学者来说，社区版完全足够日常学习和开发使用。\r\n\r\n### 为什么选择PyCharm？\r\n\r\n- **智能代码补全**：输入一半的代码，PyCharm会给出合理的补全建议\r\n- **错误检测**：实时标记代码中的错误，帮助你及时改正\r\n- **调试工具**：可以一步步执行代码，观察变量的变化\r\n- **项目管理**：方便管理多个文件和模块\r\n- **集成终端**：无需离开PyCharm就可以使用命令行\r\n\r\n\r\n\r\n## 二、安装PyCharm\r\n\r\n1. **下载安装包**\r\n   - 访问官方网站：https://www.jetbrains.com/pycharm/download/\r\n   - 选择\"Community\"(社区版)\r\n   - 点击下载Windows版本的安装包\r\n2. **运行安装程序**\r\n   - 双击下载好的.exe文件\r\n   - 点击\"Next\"开始安装过程\r\n3. **选择安装位置**\r\n   - 默认安装位置通常在C盘的Program Files目录下\r\n   - 如果C盘空间不足，可以选择其他磁盘\r\n4. **选择安装选项**\r\n   - 勾选\"Create Desktop Shortcut\"创建桌面快捷方式\r\n   - 勾选\"Update PATH variable\"以便在命令行中使用PyCharm\r\n   - 勾选\"Create Associations\"关联.py文件（可选）\r\n5. **完成安装**\r\n   - 点击\"Install\"开始安装\r\n   - 等待安装完成后点击\"Finish\"\r\n\r\n\r\n\r\n## 三、首次启动配置\r\n\r\n### 1. 首次启动欢迎界面\r\n\r\n首次启动PyCharm时，会看到欢迎界面，包含以下选项：\r\n\r\n- 导入设置（如果以前使用过）\r\n- 选择UI主题（浅色或深色）\r\n- 创建新项目或打开已有项目\r\n\r\n### 2. 配置Python解释器\r\n\r\n创建新项目时，需要配置Python解释器：\r\n\r\n1. 点击\"New Project\"\r\n2. 选择项目位置\r\n3. 在\"Python Interpreter\"部分：\r\n   - 使用\"Previously configured interpreter\"选择已安装的Python\r\n   - 或选择\"New environment using Virtualenv\"创建虚拟环境（推荐）\r\n4. 点击\"Create\"创建项目\r\n\r\n> **小贴士**：虚拟环境是一个独立的Python环境，可以为每个项目安装不同版本的包，避免包版本冲突。对初学者来说，使用虚拟环境是一个好习惯。\r\n\r\n\r\n\r\n## 四、PyCharm界面介绍\r\n\r\n### 主要界面组成\r\n\r\nPyCharm的界面主要由以下几部分组成：\r\n\r\n1. **项目工具窗口(Project Tool Window)**：左侧，显示项目文件结构\r\n2. **编辑器(Editor)**：中央区域，用于编写代码\r\n3. **工具栏(Toolbar)**：顶部，提供常用操作按钮\r\n4. **状态栏(Status Bar)**：底部，显示当前文件和项目状态\r\n5. **终端(Terminal)**：底部，集成的命令行窗口\r\n6. **运行窗口(Run Window)**：底部，显示程序运行结果\r\n\r\n### 自定义界面\r\n\r\n可以根据个人喜好调整界面：\r\n\r\n- **调整工具窗口大小**：拖动分隔线\r\n- **隐藏/显示工具窗口**：点击窗口边缘的工具按钮\r\n- **更改主题**：File > Settings > Appearance & Behavior > Appearance\r\n\r\n\r\n\r\n## 五、创建第一个Python程序\r\n\r\n### 1. 创建新文件\r\n\r\n1. 右键点击项目名称\r\n2. 选择 New > Python File\r\n3. 输入文件名（如`hello.py`）并按回车\r\n\r\n### 2. 编写代码\r\n\r\n在新创建的文件中输入以下代码：\r\n\r\n```python\r\nprint(\"Hello, PyCharm!\")\r\n\r\n# 简单的计算示例\r\na = 10\r\nb = 20\r\nsum_result = a + b\r\nprint(f\"计算结果: {a} + {b} = {sum_result}\")\r\n\r\n# 获取用户输入\r\nname = input(\"请输入你的名字: \")\r\nprint(f\"你好，{name}！欢迎使用PyCharm！\")\r\n```\r\n\r\n### 3. 运行程序\r\n\r\n有多种方法可以运行程序：\r\n\r\n1. **方法一**：右键点击编辑器，选择\"Run \'hello\'\"\r\n2. **方法二**：点击编辑器右上角的绿色三角形图标\r\n3. **方法三**：使用快捷键`Shift+F10`(Windows/Linux)或`Ctrl+R`(Mac)\r\n\r\n程序运行后，结果会显示在底部的运行窗口中。\r\n\r\n\r\n\r\n## 六、PyCharm基本功能操作\r\n\r\n### 代码编辑技巧\r\n\r\n1. **代码补全**：\r\n   - 输入代码的前几个字母，按`Ctrl+Space`调出补全菜单\r\n   - PyCharm会根据上下文智能推荐可能的补全选项\r\n2. **快速修复**：\r\n   - 当代码有错误时，会用红色波浪线标记\r\n   - 将光标放在错误处，按`Alt+Enter`查看修复建议\r\n3. **代码格式化**：\r\n   - 选中代码，按`Ctrl+Alt+L`(Windows/Linux)或`Cmd+Alt+L`(Mac)\r\n   - 自动调整代码格式，使其符合PEP 8规范\r\n4. **查看文档**：\r\n   - 将光标放在函数或类上，按`Ctrl+Q`(Windows/Linux)或`F1`(Mac)\r\n   - 显示该函数或类的文档说明\r\n\r\n### 文件和项目管理\r\n\r\n1. **创建新文件夹**：\r\n   - 右键点击项目，选择New > Directory\r\n2. **重命名文件**：\r\n   - 右键点击文件，选择Refactor > Rename\r\n   - 或按`Shift+F6`\r\n3. **搜索项目文件**：\r\n   - 按`Ctrl+Shift+N`(Windows/Linux)或`Cmd+Shift+O`(Mac)\r\n   - 输入文件名的一部分即可快速找到文件\r\n\r\n### 调试程序\r\n\r\n调试是解决程序问题的有效方法：\r\n\r\n1. **设置断点**：\r\n   - 点击代码行号旁边的空白区域，会出现一个红点\r\n   - 或将光标放在某行，按`Ctrl+F8`(Windows/Linux)或`Cmd+F8`(Mac)\r\n2. **开始调试**：\r\n   - 点击绿色虫子图标\r\n   - 或按`Shift+F9`(Windows/Linux)或`Ctrl+D`(Mac)\r\n3. **调试控制**：\r\n   - 继续执行(F9)：程序继续执行到下一个断点\r\n   - 单步执行(F8)：执行当前行，进入下一行\r\n   - 步入(F7)：进入当前行调用的函数内部\r\n   - 步出(Shift+F8)：执行完当前函数，返回到调用处\r\n4. **查看变量**：\r\n   - 调试时，底部的\"Variables\"窗口会显示当前所有变量的值\r\n   - 可以实时观察变量如何变化\r\n\r\n\r\n\r\n## 七、安装和管理Python包\r\n\r\n### 使用PyCharm安装包\r\n\r\n1. **打开设置**：\r\n   - 选择File > Settings(Windows/Linux)或PyCharm > Preferences(Mac)\r\n2. **找到项目解释器**：\r\n   - 导航到Project: [项目名] > Python Interpreter\r\n3. **安装包**：\r\n   - 点击\"+\"按钮\r\n   - 在搜索框中输入包名（如\"numpy\"）\r\n   - 选择版本，点击\"Install Package\"\r\n\r\n### 使用终端安装包\r\n\r\n1. **打开终端**：\r\n\r\n   - 点击底部的\"Terminal\"标签\r\n\r\n2. **使用pip安装**：\r\n\r\n   ```bash\r\n   pip install package_name\r\n   ```\r\n\r\n3. **验证安装**：\r\n\r\n   ```bash\r\n   pip list\r\n   ```\r\n\r\n\r\n\r\n## 八、实用技巧和快捷键\r\n\r\n掌握一些常用快捷键可以大大提高编程效率：\r\n\r\n### 常用快捷键(Windows/Linux)\r\n\r\n- `Ctrl+D`：复制当前行\r\n- `Ctrl+Y`：删除当前行\r\n- `Ctrl+/`：注释/取消注释\r\n- `Ctrl+Shift+F`：在项目中搜索\r\n- `Alt+Enter`：显示意图操作和快速修复\r\n- `Ctrl+Shift+A`：查找操作\r\n- `Ctrl+Tab`：在打开的文件之间切换\r\n\r\n### 常用快捷键(Mac)\r\n\r\n- `Cmd+D`：复制当前行\r\n- `Cmd+删除`：删除当前行\r\n- `Cmd+/`：注释/取消注释\r\n- `Cmd+Shift+F`：在项目中搜索\r\n- `Option+Enter`：显示意图操作和快速修复\r\n- `Cmd+Shift+A`：查找操作\r\n- `Ctrl+Tab`：在打开的文件之间切换\r\n\r\n> **小贴士**：可以在Help > Keymap Reference中查看完整的快捷键列表。\r\n\r\n\r\n\r\n## 九、常见问题解决\r\n\r\n### 1. PyCharm启动缓慢\r\n\r\n**解决方法**：\r\n\r\n- 增加PyCharm的内存分配：Help > Edit Custom VM Options\r\n- 减少项目索引的文件：排除不需要索引的文件夹（如venv）\r\n- 关闭不必要的插件：File > Settings > Plugins\r\n\r\n### 2. 无法识别已安装的包\r\n\r\n**解决方法**：\r\n\r\n- 检查使用的是否是正确的Python解释器\r\n- 重新安装该包\r\n- 使用File > Invalidate Caches / Restart重新构建索引\r\n\r\n### 3. 代码提示不工作\r\n\r\n**解决方法**：\r\n\r\n- 检查是否关闭了代码补全功能\r\n- 尝试手动触发补全：Ctrl+Space\r\n- 重建索引：File > Invalidate Caches / Restart', '适合初学者的Pycharm安装和使用教程', 2, 'https://www.runoob.com/wp-content/uploads/2025/04/0_YoOahLRY1XQ-XG34.png', '1', '0', 115, '0', NULL, '2023-04-22 14:58:34', NULL, NULL, 0);
INSERT INTO `article` VALUES (4, 'Spring Boot实现验证码生成及验证功能', '## 1. 验证码介绍\r\n\r\n验证码（CAPTCHA）是Completely Automated Public Turing test to tell Computers and Humans Apart（全自动区分计算机和人类的图灵测试）的缩写，主要作用是防止不法分子在短时间内用机器进行批量的重复操作给网站带来破坏，从而保护系统的安全。\r\n\r\n\r\n\r\n验证码的形式：\r\n\r\n1. 传统输入式验证码\r\n2. 手机短信验证码\r\n3. 图片识别与选择型验证码\r\n4. 滑块类型验证码\r\n5. 其他类型验证码，如指纹识别、人脸识别等\r\n\r\n\r\n\r\n## 2. Spring Boot整合easy-captcha生成验证码\r\n\r\n> **添加easy-captcha依赖**\r\n\r\neasy-captcha是一款国人开发的验证码工具，支持GIF、中文、算术等类型，可用于Java Web等项目\r\n\r\n```xml\r\n<dependency>\r\n    <groupId>com.github.whvcse</groupId>\r\n    <artifactId>easy-captcha</artifactId>\r\n    <version>1.6.2</version>\r\n</dependency>\r\n```\r\n\r\n\r\n\r\n> **流程**\r\n\r\n* 在后端生成验证码后，对当前生成的验证码内容进行保存，可以选择保存在session对象中，或者保存在Redis缓存中等。然后，返回验证码图片并显示到前端页面。\r\n\r\n* 用户在识别验证码后，在页面对应的输入框中填写验证码并向后端发送请求，后端在接到请求后会对用户输入的验证码进行验证。\r\n* 如果用户输入的验证码与之前保存的验证码不相等的话，则返回“验证码错误”的提示消息且不会进行后续的流程，只有验证成功才会继续后续的流程。\r\n\r\n\r\n\r\n> **生成并显示验证码**\r\n\r\n1. 在static目录中新建 `captcha.html` 页面，在该页面中显示验证码\r\n\r\n   ```html\r\n   <!DOCTYPE html>\r\n   <html lang=\"en\">\r\n   <head>\r\n       <meta charset=\"UTF-8\"/>\r\n       <title>验证码显示</title>\r\n   </head>\r\n   <body>\r\n   <img src=\"/captcha\" onclick=\"this.src=\'/captcha\'\"/>\r\n   </body>\r\n   </html>\r\n   ```\r\n\r\n2. 在controller包中新建 `CaptchaController` 类。在 `generateCaptcha()` 方法里使用SpecCaptcha可以生成一个PNG类型的验证码对象，并以图片流的方式输出到前端以供显示\r\n\r\n   ```java\r\n   @RequestMapping(\"/captcha\")\r\n   public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException, FontFormatException {\r\n       // 3个设置都是为了不缓存响应数据，同时设置可以兼容不同的浏览器或缓存服务器\r\n       response.setHeader(\"Cache-Control\", \"no-store\");\r\n       response.setHeader(\"Pragma\", \"no-cache\");\r\n       response.setDateHeader(\"Expires\", 0);\r\n       // 响应内容是一个GIF格式的图像\r\n       response.setContentType(\"image/gif\");\r\n   \r\n       // 生成指定类型的验证码对象，三个参数分别为宽、高、位数\r\n       // png类型\r\n       SpecCaptcha captcha = new SpecCaptcha(300, 120,4);\r\n       // gif类型\r\n       //        GifCaptcha captcha = new GifCaptcha(300, 120, 4);\r\n       // 中文类型\r\n       //        ChineseCaptcha captcha = new ChineseCaptcha(300, 120,4);\r\n       // 中文gif类型\r\n       //        ChineseGifCaptcha captcha = new ChineseGifCaptcha(300, 120,4);\r\n       // 算术类型\r\n       //        ArithmeticCaptcha captcha = new ArithmeticCaptcha(300, 120, 5);\r\n   \r\n       // 设置字符类型\r\n       captcha.setCharType(Captcha.TYPE_DEFAULT);\r\n   \r\n       // 设置字体\r\n       captcha.setFont(Captcha.FONT_8, 40);\r\n   \r\n       // 验证码存入 session\r\n       request.getSession().setAttribute(\"verifyCode\", captcha.text().toLowerCase());\r\n   \r\n       // 输出图片流\r\n       captcha.out(response.getOutputStream());\r\n   }\r\n   ```\r\n\r\n\r\n\r\n> **验证码的输入验证**\r\n\r\n1. 在static目录中新建 `verify.html`，该页面会显示验证码，同时也包含供用户输入验证码的输入框和提交按钮\r\n\r\n   ```html\r\n   <!DOCTYPE html>\r\n   <html lang=\"en\">\r\n   <head>\r\n       <meta charset=\"UTF-8\"/>\r\n       <title>验证码测试</title>\r\n   </head>\r\n   <body>\r\n   <img src=\"/captcha\" onclick=\"this.src=\'/captcha\'\"/>\r\n   <br>\r\n   <input type=\"text\" maxlength=\"5\" id=\"code\" placeholder=\"请输入验证码\"/>\r\n   <button id=\"verify\">验证</button>\r\n   <br>\r\n   <p id=\"verifyResult\">\r\n   </p>\r\n   </body>\r\n   \r\n   <script src=\"//code.jquery.com/jquery-1.11.3.min.js\"></script>\r\n   <script>\r\n       $(\'#verify\').click(function () {\r\n           var code = $(\'#code\').val();\r\n           $.ajax({\r\n               type: \'GET\',\r\n               url: \'/verify\',\r\n               data: {\"code\": code},\r\n               success: function (result) {\r\n                   $(\'#verifyResult\').html(result);\r\n               },\r\n               error: function () {\r\n                   alert(\'请求失败\');\r\n               },\r\n           });\r\n       });\r\n   </script>\r\n   </html>\r\n   ```\r\n\r\n2. 在 `CaptchaController` 类中新增 `verify()` 方法\r\n\r\n   * **HttpSession**：可以作为参数直接传入方法，用于在会话中存储和获取属性\r\n\r\n   ```java\r\n   @RequestMapping(\"/verify\")\r\n   @ResponseBody\r\n   public String verify(String code, HttpSession session) {\r\n       if (!StringUtils.hasLength(code)) {\r\n           return \"验证码不能为空\";\r\n       }\r\n       String verifyCode = session.getAttribute(\"verifyCode\") + \"\";\r\n       if (!code.toLowerCase().equals(verifyCode)) {\r\n           return \"验证码错误\";\r\n       }\r\n       return \"验证成功\";\r\n   }\r\n   ```\r\n\r\n   \r\n\r\n\r\n\r\n\r\n\r\n', '使用Spring Boot生成验证码并进行后续的验证操作', 1, 'https://img0.baidu.com/it/u=41156083,2231346161&fm=253&fmt=auto&app=138&f=GIF?w=539&h=389', '0', '0', 44, '0', NULL, '2023-04-25 15:22:30', NULL, NULL, 0);

-- ----------------------------
-- Table structure for article_tag
-- ----------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag`  (
  `article_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文章id',
  `tag_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '标签id',
  PRIMARY KEY (`article_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文章标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of article_tag
-- ----------------------------
INSERT INTO `article_tag` VALUES (1, 1);
INSERT INTO `article_tag` VALUES (1, 3);
INSERT INTO `article_tag` VALUES (2, 4);
INSERT INTO `article_tag` VALUES (3, 4);
INSERT INTO `article_tag` VALUES (4, 1);
INSERT INTO `article_tag` VALUES (4, 3);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint(200) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类名',
  `pid` bigint(200) NULL DEFAULT -1 COMMENT '父分类id，如果没有父分类为-1',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态0:正常,1禁用',
  `create_by` bigint(200) NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` bigint(200) NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int(11) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, 'Java', -1, 'java相关', '0', NULL, NULL, NULL, NULL, 0);
INSERT INTO `category` VALUES (2, 'Python', -1, 'python相关', '0', NULL, NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '评论类型（0代表文章评论，1代表友链评论）',
  `article_id` bigint(20) NULL DEFAULT NULL COMMENT '文章id',
  `root_id` bigint(20) NULL DEFAULT -1 COMMENT '根评论id',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评论内容',
  `to_comment_user_id` bigint(20) NULL DEFAULT -1 COMMENT '所回复的目标评论的userid',
  `to_comment_id` bigint(20) NULL DEFAULT -1 COMMENT '回复目标评论id',
  `create_by` bigint(20) NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` bigint(20) NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, '1', NULL, -1, '申请友链请留下网站名称和地址', -1, -1, 1, '2023-05-16 12:05:33', 1, '2023-05-16 12:05:33', 0);
INSERT INTO `comment` VALUES (2, '0', 1, -1, '这篇文章写得真好', -1, -1, 1, '2023-05-16 12:06:38', 1, '2023-05-16 12:06:38', 0);
INSERT INTO `comment` VALUES (3, '0', 1, 2, '学到很多', 1, 2, 3, '2023-05-16 12:08:05', 3, '2023-05-16 12:08:05', 0);
INSERT INTO `comment` VALUES (4, '1', NULL, 1, '网站做得很漂亮，想申请一个友链', 1, 1, 3, '2023-05-16 12:08:21', 3, '2023-05-16 12:08:21', 0);
INSERT INTO `comment` VALUES (5, '0', 1, 2, '👍', 3, 3, 1, '2023-05-16 12:09:02', 1, '2023-05-16 12:09:02', 0);
INSERT INTO `comment` VALUES (6, '1', NULL, 1, '👌', 3, 4, 1, '2023-05-16 12:09:18', 1, '2023-05-16 12:09:18', 0);

-- ----------------------------
-- Table structure for link
-- ----------------------------
DROP TABLE IF EXISTS `link`;
CREATE TABLE `link`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `logo` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `address` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '网站地址',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '2' COMMENT '审核状态 (0代表审核通过，1代表审核未通过，2代表未审核)',
  `create_by` bigint(20) NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` bigint(20) NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '友链' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of link
-- ----------------------------
INSERT INTO `link` VALUES (1, '百度', 'https://storage-public.zhaopin.cn/user/avatar/1583843473427893323/3b455a32-17d4-4e39-b5b6-fa04a88c36ed.jpg', 'Baidu', 'https://www.baidu.com', '0', NULL, '2023-03-23 08:26:42', NULL, '2023-03-24 08:36:14', 0);
INSERT INTO `link` VALUES (2, '腾讯', 'https://bpic.51yuansu.com/pic3/cover/00/69/38/58ab18eda37be_610.jpg', 'Tencent', 'https://www.qq.com', '0', NULL, '2023-03-23 08:12:01', NULL, '2023-03-24 09:07:09', 0);
INSERT INTO `link` VALUES (3, '淘宝', 'https://img.zcool.cn/community/01febc5ff2bced11013ee04d9e732a.jpg', 'Taobao', 'https://www.taobao.com', '0', NULL, '2023-03-23 13:10:11', NULL, '2023-03-24 09:23:01', 1);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `is_frame` int(1) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `menu_type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2029 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, 1, 'M', '0', '0', '', 'system', 0, '2021-11-12 10:46:19', 0, NULL, '系统管理目录', '0');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', 1, 'C', '0', '0', 'system:user:list', 'user', 0, '2021-11-12 10:46:19', 1, '2022-07-31 15:47:58', '用户管理菜单', '0');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', 1, 'C', '0', '0', 'system:role:list', 'peoples', 0, '2021-11-12 10:46:19', 0, NULL, '角色管理菜单', '0');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', 1, 'C', '0', '0', 'system:menu:list', 'tree-table', 0, '2021-11-12 10:46:19', 0, NULL, '菜单管理菜单', '0');
INSERT INTO `sys_menu` VALUES (1001, '用户查询', 100, 1, '', '', 1, 'F', '0', '0', 'system:user:query', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1002, '用户新增', 100, 2, '', '', 1, 'F', '0', '0', 'system:user:add', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1003, '用户修改', 100, 3, '', '', 1, 'F', '0', '0', 'system:user:edit', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1004, '用户删除', 100, 4, '', '', 1, 'F', '0', '0', 'system:user:remove', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1005, '用户导出', 100, 5, '', '', 1, 'F', '0', '0', 'system:user:export', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1006, '用户导入', 100, 6, '', '', 1, 'F', '0', '0', 'system:user:import', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1007, '重置密码', 100, 7, '', '', 1, 'F', '0', '0', 'system:user:resetPwd', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1008, '角色查询', 101, 1, '', '', 1, 'F', '0', '0', 'system:role:query', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1009, '角色新增', 101, 2, '', '', 1, 'F', '0', '0', 'system:role:add', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1010, '角色修改', 101, 3, '', '', 1, 'F', '0', '0', 'system:role:edit', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1011, '角色删除', 101, 4, '', '', 1, 'F', '0', '0', 'system:role:remove', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1012, '角色导出', 101, 5, '', '', 1, 'F', '0', '0', 'system:role:export', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1013, '菜单查询', 102, 1, '', '', 1, 'F', '0', '0', 'system:menu:query', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1014, '菜单新增', 102, 2, '', '', 1, 'F', '0', '0', 'system:menu:add', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1015, '菜单修改', 102, 3, '', '', 1, 'F', '0', '0', 'system:menu:edit', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (1016, '菜单删除', 102, 4, '', '', 1, 'F', '0', '0', 'system:menu:remove', '#', 0, '2021-11-12 10:46:19', 0, NULL, '', '0');
INSERT INTO `sys_menu` VALUES (2017, '内容管理', 0, 4, 'content', NULL, 1, 'M', '0', '0', NULL, 'table', NULL, '2022-01-08 02:44:38', 1, '2022-07-31 12:34:23', '', '0');
INSERT INTO `sys_menu` VALUES (2018, '分类管理', 2017, 1, 'category', 'content/category/index', 1, 'C', '0', '0', 'content:category:list', 'example', NULL, '2022-01-08 02:51:45', NULL, '2022-01-08 02:51:45', '', '0');
INSERT INTO `sys_menu` VALUES (2019, '文章管理', 2017, 0, 'article', 'content/article/index', 1, 'C', '0', '0', 'content:article:list', 'build', NULL, '2022-01-08 02:53:10', NULL, '2022-01-08 02:53:10', '', '0');
INSERT INTO `sys_menu` VALUES (2021, '标签管理', 2017, 6, 'tag', 'content/tag/index', 1, 'C', '0', '0', 'content:tag:index', 'button', NULL, '2022-01-08 02:55:37', NULL, '2022-01-08 02:55:50', '', '0');
INSERT INTO `sys_menu` VALUES (2022, '友链管理', 2017, 4, 'link', 'content/link/index', 1, 'C', '0', '0', 'content:link:list', '404', NULL, '2022-01-08 02:56:50', NULL, '2022-01-08 02:56:50', '', '0');
INSERT INTO `sys_menu` VALUES (2023, '写博文', 0, 0, 'write', 'content/article/write/index', 1, 'C', '0', '0', 'content:article:writer', 'build', NULL, '2022-01-08 03:39:58', 1, '2022-07-31 22:07:05', '', '0');
INSERT INTO `sys_menu` VALUES (2024, '友链新增', 2022, 0, '', NULL, 1, 'F', '0', '0', 'content:link:add', '#', NULL, '2022-01-16 07:59:17', NULL, '2022-01-16 07:59:17', '', '0');
INSERT INTO `sys_menu` VALUES (2025, '友链修改', 2022, 1, '', NULL, 1, 'F', '0', '0', 'content:link:edit', '#', NULL, '2022-01-16 07:59:44', NULL, '2022-01-16 07:59:44', '', '0');
INSERT INTO `sys_menu` VALUES (2026, '友链删除', 2022, 1, '', NULL, 1, 'F', '0', '0', 'content:link:remove', '#', NULL, '2022-01-16 08:00:05', NULL, '2022-01-16 08:00:05', '', '0');
INSERT INTO `sys_menu` VALUES (2027, '友链查询', 2022, 2, '', NULL, 1, 'F', '0', '0', 'content:link:query', '#', NULL, '2022-01-16 08:04:09', NULL, '2022-01-16 08:04:09', '', '0');
INSERT INTO `sys_menu` VALUES (2028, '导出分类', 2018, 1, '', NULL, 1, 'F', '0', '0', 'content:category:export', '#', NULL, '2022-01-21 07:06:59', NULL, '2022-01-21 07:06:59', '', '0');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '0', '0', 0, '2021-11-12 10:46:19', 0, NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '0', '0', 0, '2021-11-12 10:46:19', 0, '2022-01-01 22:32:58', '普通角色');
INSERT INTO `sys_role` VALUES (3, '友链审核员', 'link', 1, '0', '0', NULL, '2022-01-16 06:49:30', NULL, '2022-01-16 08:05:09', NULL);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 0);
INSERT INTO `sys_role_menu` VALUES (2, 2017);
INSERT INTO `sys_role_menu` VALUES (2, 2018);
INSERT INTO `sys_role_menu` VALUES (2, 2019);
INSERT INTO `sys_role_menu` VALUES (2, 2021);
INSERT INTO `sys_role_menu` VALUES (2, 2023);
INSERT INTO `sys_role_menu` VALUES (2, 2028);
INSERT INTO `sys_role_menu` VALUES (3, 2017);
INSERT INTO `sys_role_menu` VALUES (3, 2022);
INSERT INTO `sys_role_menu` VALUES (3, 2024);
INSERT INTO `sys_role_menu` VALUES (3, 2025);
INSERT INTO `sys_role_menu` VALUES (3, 2026);
INSERT INTO `sys_role_menu` VALUES (3, 2027);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 3);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签名',
  `create_by` bigint(20) NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_by` bigint(20) NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` int(1) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '标签' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, 'SpringBoot', NULL, '2022-01-11 09:20:50', NULL, '2022-01-11 09:20:50', 0, '');
INSERT INTO `tag` VALUES (2, 'Web开发', NULL, '2022-01-11 09:20:55', NULL, '2022-01-11 09:20:55', 1, '');
INSERT INTO `tag` VALUES (3, 'Java', NULL, '2022-01-11 09:21:07', NULL, '2022-01-11 09:21:07', 0, '');
INSERT INTO `tag` VALUES (4, 'Python', NULL, '2022-01-13 15:22:43', NULL, '2022-01-13 15:22:43', 0, '');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NULL' COMMENT '用户名',
  `nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NULL' COMMENT '昵称',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'NULL' COMMENT '密码',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户类型：0代表普通用户，1代表管理员',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phonenumber` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户性别（0男，1女，2未知）',
  `avatar` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建人的用户id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` int(11) NULL DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'admin', '$2a$10$Jnq31rRkNV3RNzXe0REsEOSKaYK8UgVZZqlNlNXqn.JeVcj2NdeZy', '1', '0', '', '', '1', '', NULL, '2023-02-01 09:21:12', NULL, '2023-02-01 09:25:22', 0);
INSERT INTO `user` VALUES (2, 'test', 'test', '$2a$10$Jnq31rRkNV3RNzXe0REsEOSKaYK8UgVZZqlNlNXqn.JeVcj2NdeZy', '0', '0', 'test@qq.com', '16666666666', '0', 'https://gss0.baidu.com/-Po3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/574e9258d109b3de57070594cbbf6c81810a4c96.jpg', NULL, '2023-03-02 14:16:33', NULL, '2023-03-02 18:11:31', 0);
INSERT INTO `user` VALUES (3, 'ptu', '小胖', '$2a$10$Jnq31rRkNV3RNzXe0REsEOSKaYK8UgVZZqlNlNXqn.JeVcj2NdeZy', '0', '0', 'ptu@ptu.edu.cn', '18888888888', '0', NULL, NULL, '2023-03-02 15:14:32', NULL, '2023-03-02 15:14:40', 0);

SET FOREIGN_KEY_CHECKS = 1;
