#!/bin/bash

# Update WebServlet annotations
find src/main/java -name "*.java" -type f -exec sed -i 's/@WebServlet/@javax.servlet.annotation.WebServlet/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/@MultipartConfig/@javax.servlet.annotation.MultipartConfig/g' {} \;

echo "All servlet annotations updated"