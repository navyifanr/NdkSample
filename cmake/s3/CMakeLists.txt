cmake_minimum_required(VERSION 3.9)

project(HELLO)
set(LIB_SRC hello.c)
set(APP_SRC main.c)
add_library(libhello ${LIB_SRC})
set_target_properties(libhello PROPERTIES OUTPUT_NAME "hello")

add_executable(hello ${APP_SRC})
target_link_libraries(hello libhello)

