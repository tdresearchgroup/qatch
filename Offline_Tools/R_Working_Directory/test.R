
library("xlsx")


args <- commandArgs(trailingOnly = TRUE)
Dir <- args[1]
setwd(Dir)
matrix_file = "/Users/guribhangu/development/research/qatch/Comparison_Matrices/compatibility.xlsx"
#read.matrix(matxix_file, header = FALSE, sep = "", skip = 0)
#df <- read.xlsx(matrix_file, sheetIndex = 1, header = TRUE, stringsAsFactors=FALSE)
#x <- matrix(df)
#x <- read.matrix(matrix_file)
#print(x)




df <- read.xlsx(matrix_file, sheetIndex = 1, header = FALSE, stringsAsFactors=FALSE)
#print(df[1, 1])

# Complete the main diagonal with ones
for(i in seq(1,ncol(df))){
  df[[i,i]] <- as.numeric(1)
}

# Complete the lower triangle with the reciprosal values of the upper
for(i in c(2:nrow(df))){
    for(j in seq(1,i-1)){
     # print(paste("i= ", as.character(i), " j= ", as.character(j)))
     # print(seq(1,i-1))
     # print(sub.df[[i,j]])
      if(df[[j,i]] != 0){
        df[[i,j]] = 1 / as.numeric(df[[j,i]])
      }else{
        print("Devision by zero avoided")
      }
    }
}

matrix = sapply(df[2:12, 2:12], function(x){as.numeric(x)})
print(matrix)
ev <- eigen(matrix)
# get characteristic name
characteristic <- df[1, 1]
#print(characteristic)

A <- matrix(c(13, -4, 2, -4, 11, -2, 2, -2, 8), 3, 3, byrow=TRUE)
#print(A)
ev <- eigen(A)

#values <- ev$values
#vectors <- ev$vectors
#print(values)
#print(vectors)

#print(ev)

weights <- ev$vectors / sum(ev$vectors)
#print(weights)
weights <- as.numeric(weights)
#print(weights)

l <- list(weights)
#print(l)
l <- c(l, list(weights))
#print(l)
