#!/bin/bash

# Update imports in all Java files
find src/main/java -name "*.java" -type f -exec sed -i 's/import jakarta.servlet/import javax.servlet/g' {} \;
find src/main/java -name "*.java" -type f -exec sed -i 's/import jakarta.annotation/import javax.annotation/g' {} \;

echo "All servlet imports updated from 'jakarta' to 'javax'"