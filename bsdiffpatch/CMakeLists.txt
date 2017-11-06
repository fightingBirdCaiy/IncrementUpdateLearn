cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             bsdiff

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/bsdiff.c )

add_library( # Sets the name of the library.
             bspatch

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/bspatch.c )

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( # Specifies the target library.
                       bsdiff

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

target_link_libraries( # Specifies the target library.
                       bspatch

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

include_directories(src/main/cpp/bzip2/)
