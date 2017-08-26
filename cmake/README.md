#这是CMake练习的Samples
---------------------

例子1：单个源文件 main.c

例子2：分解成多个 main.c hello.h hello.c

例子3：先生成一个静态库，链接该库

例子4：将源文件放置到不同的目录

例子5：控制生成的程序和库所在的目录

例子6：使用动态库而不是静态库


以上例子，执行的步骤：
- 1. 进入对应例子的文件夹（如 s1），用 `mkdir build` 创建一个 build 文件夹，并进入 build 文件夹；
- 2. 执行 `cmake ..` 命令（前提安装好 cmake）
- 3. 执行 `make` 命令

即可在 build 文件夹找到生成的可执行文件或库

具体例子的介绍文章参考：[cmake 学习笔记(一)](http://blog.csdn.net/dbzhang800/article/details/6314073)

另外，介绍 CMake 的文章可查看：[Android NDK 开发：CMake 学习](http://cfanr.cn/2017/08/26/Android-NDK-dev-CMake-s-usage/)