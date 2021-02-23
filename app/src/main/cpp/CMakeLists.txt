# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html
cmake_minimum_required(VERSION 3.10.2)

project("myapplication")

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).

        native-lib.cpp
        )

find_library(log-lib log)

target_link_libraries(native-lib ${log-lib})



