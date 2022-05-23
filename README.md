# Minimum Colours
Each colour performs a complete subgraph. Grouping nodes so that there is a minimum number of colours that cross the midpoint line.

# Solution
```Time taken: 0.4213 milli seconds
Group 1: 4, 5, 2, 3
Group 2: 0, 1, 6, 7
Minimum colours cross: 1

Time taken: 2.4365 milli seconds
Group 1: 0, 1, 7, 3, 4
Group 2: 5, 6, 2, 8, 9
Minimum colours cross: 22

Time taken: 14.7657 milli seconds
Group 1: 0, 1, 22, 23, 4, 5, 6, 7, 28, 29, 10, 11, 12, 33, 14, 35, 16, 37, 38, 19
Group 2: 20, 21, 2, 3, 24, 25, 26, 27, 8, 9, 30, 31, 32, 13, 34, 15, 36, 17, 18, 39
Minimum colours cross: 65

Time taken: 6.6151 milli seconds
Group 1: 0, 26, 27, 3, 29, 30, 6, 7, 8, 34, 10, 11, 12, 13, 39, 15, 16, 42, 18, 44, 20, 21, 22, 23, 49
Group 2: 25, 1, 2, 28, 4, 5, 31, 32, 33, 9, 35, 36, 37, 38, 14, 40, 41, 17, 43, 19, 45, 46, 47, 48, 24
Minimum colours cross: 105

Time taken: 17.1799 milli seconds
Group 1: 30, 31, 2, 3, 4, 5, 36, 7, 8, 9, 10, 11, 42, 13, 44, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29
Group 2: 0, 1, 32, 33, 34, 35, 6, 37, 38, 39, 40, 41, 12, 43, 14, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59
Minimum colours cross: 229

Time taken: 34.1816 milli seconds
Group 1: 30, 1, 32, 33, 4, 35, 6, 7, 8, 9, 10, 11, 12, 13, 44, 45, 16, 17, 18, 49, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29
Group 2: 0, 31, 2, 3, 34, 5, 36, 37, 38, 39, 40, 41, 42, 43, 14, 15, 46, 47, 48, 19, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59
Minimum colours cross: 479
```
