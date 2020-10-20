
# Load the appropriate libraries
library("xlsx")

# Move to the desired directory where the comparison matrices are placed
args <- commandArgs(trailingOnly = TRUE)
Dir <- args[1]
setwd(Dir)

# Create a list with all the calculated RIs
ri <- c(0, 0, 0.52, 0.89, 1.11, 1.25, 1.35, 1.4, 1.45, 1.49)

# List the files found in this directory
files <- dir("./Comparison_Matrices")
setwd("./Comparison_Matrices")

first <- TRUE

# Iterate through each file found in the directory
for(file in files){
  print(file)
  # Read the xls and store its values in a dataframe
  # Read the data frame
  df <- read.xlsx(file, sheetIndex = 1, header = TRUE, stringsAsFactors=FALSE)
 
  
  # Keep only the values
  sub.df <- df[, -1]
  sapply(sub.df,as.numeric)
  
  # Complete the main diagonal with ones
  for(i in seq(1,ncol(sub.df))){
   # print(sub.df[[i,i]])
    sub.df[[i,i]] <- as.numeric(1)
  }
  
  # Complete the lower triangle with the reciprosal values of the upper
  for(i in c(2:nrow(df))){
    for(j in seq(1,i-1)){
     # print(paste("i= ", as.character(i), " j= ", as.character(j)))
     # print(seq(1,i-1))
     # print(sub.df[[i,j]])
      if(sub.df[[j,i]] != 0){
        sub.df[[i,j]] = 1 / as.numeric(sub.df[[j,i]])
      }else{
        print("Devision by zero avoided")
      }
    }
  }
  
  # Calculate eigenvalues/eigenvectors -> weights
  eig <- eigen(data.matrix(sub.df))
  vec <- eig$vectors[,1]
  s <- sum(vec)
  weights <- vec / s
  weights <- as.numeric(weights)
  
  # Add them to a list
  if(first){
    l <- list(weights)
    n <- names(df)
    char.names <- list(n[[1]])
    first <- FALSE
  }else{
    l <- c(l, list(weights))
    n <- names(df)
    char.names <- c( char.names , n[[1]])
  }
  
  #Calculate the consistency
  CI <- (eig$values[[1]]-nrow(sub.df))/(nrow(sub.df)-1)
  print(paste("THE CI = ", as.character(CI)))
  
  if(length(weights) > 2 & length(weights) <= 10){
    print(ri[length(weights)])
    CR <- CI / ri[length(weights)]
    print(paste("THE CR = ", as.character(CR)))
  }else{
    print("CR canno't be calculated")
  }
  
}

# Set the name of each characteristic
names(l) <- char.names

# Store the results to a json file
library("jsonlite")
json <- toJSON(l)

setwd(Dir)
setwd("./R_Working_Directory")
write(json, "./weights.json")