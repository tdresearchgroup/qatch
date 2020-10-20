
# Load the appropriate libraries
library("xlsx")

# Set the working directory to the appropriate path
args <- commandArgs(trailingOnly = TRUE)
Dir <- args[1]
setwd(Dir)

# List the files found in this directory
files <- dir("./Comparison_Matrices")
setwd("./Comparison_Matrices")
# Create a list with all the calculated RIs
ri <- c(0, 0, 0.52, 0.89, 1.11, 1.25, 1.35, 1.4, 1.45, 1.49)

first <- TRUE

# For each comparison matrix do...
for(file in files){
  
  # Print some messages
  # print(file)
  
  # Load the comparison matrix
  df <- read.xlsx(file, sheetIndex = 1, header = TRUE, stringsAsFactors=FALSE)
  
  # Remove the first column with the names and turn everything into character
  # Keep only the values
  sub.df <- df[, -1]
  sub.df <- sapply(sub.df,as.character)
  
  # Check if the matrix is rectangular
  if(nrow(sub.df) != ncol(sub.df)){
    print("The comparison matrix is not rectangular..!!")
  }
  
  # Create the fuzzy comparison matrix
  Al <- matrix(, nrow = nrow(sub.df), ncol = ncol(sub.df))
  Am <- matrix(, nrow = nrow(sub.df), ncol = ncol(sub.df))
  Au <- matrix(, nrow = nrow(sub.df), ncol = ncol(sub.df))
  
  for(i in seq(1, nrow(sub.df) -1)){
    for(j in seq(i+1, ncol(sub.df))){
      
      #Print messages for debugging purposes...
      # print(paste("i= ", as.character(i), " j= ", as.character(j), " value= ", sub.df[i,j]))
      
      # Complete the upper triangular part of the modal comparison matrix appropriatelly
      if(grepl("Very High", sub.df[i,j])){
        Am[i,j] <- 9
      }else if(grepl("High", sub.df[i,j])){
        Am[i,j] <- 7
      }else if(grepl("Moderate", sub.df[i,j])){
        Am[i,j] <- 5
      }else if(grepl("Very Low", sub.df[i,j])){
        Am[i,j] <- 1
      }else if(grepl("Low", sub.df[i,j])){
        Am[i,j] <- 3
      }else{
       # print(paste("Found: ", sub.df[i,j]))
       # print("This is not a valid choice..!!")
        #print("Assigning the Moderate fuzzy set")
        Am[i,j] <- 5
      }
      
      # Complete the upper triangular part of matricies Al and Au
      if(grepl("C", sub.df[i,j])){
        Al[i,j] <- Am[i,j] - 0.1
        Au[i,j] <- Am[i,j] + 0.1
      }else if(grepl("D", sub.df[i,j])){
        Al[i,j] <- Am[i,j] - 0.5
        Au[i,j] <- Am[i,j] + 0.5
      }else if(grepl("U", sub.df[i,j])){
        Al[i,j] <- Am[i,j] - 0.9
        Au[i,j] <- Am[i,j] + 0.9
      }else{
        Al[i,j] <- Am[i,j] - 0.5
        Au[i,j] <- Am[i,j] + 0.5
      }
    }
  }
  
  # Placing ones to the main diagnal of the matricies
  for(i in seq(1, nrow(Am))){
    Al[i,i] <- 1;
    Am[i,i] <- 1;
    Au[i,i] <- 1;
  }
  
  # Complete the lower triangle with the reciprosal values of the upper
  for(i in c(2:nrow(Am))){
    for(j in seq(1,i-1)){
      # print(paste("i= ", as.character(i), " j= ", as.character(j)))
      # print(seq(1,i-1))
      # print(sub.df[[i,j]])
      
      Al[i,j] <- 1/Au[j,i]
      Am[i,j] <- 1/Am[j,i]
      Au[i,j] <- 1/Al[j,i]
    }
  }
  
  # Create a set of vectors for storing the weights
  wl <- numeric(nrow(Al))
  wm <- numeric(nrow(Am))
  wu <- numeric(nrow(Au))
  
  # Create the vectors for storing the multiplications
  mult.l <- rep(1, nrow(Al))
  mult.m <- rep(1, nrow(Am))
  mult.u <- rep(1, nrow(Au))
  
  # Create vectors for storing the column sums
  sum.l <- numeric(nrow(Al))
  sum.m <- numeric(nrow(Am))
  sum.u <- numeric(nrow(Au))
  
  # Calculate the multiplicants of each row
  for(i in seq(1,nrow(Am))){
    sum.l[i] <- sum(Al[,i])
    sum.m[i] <- sum(Am[,i])
    sum.u[i] <- sum(Au[,i])
    for(j in seq(1,ncol(Am))){
      
      # Calculate the sums of the columns
      # sum.l[j] <- sum.l[j] + Al[i,j]
      # sum.m[j] <- sum.m[j] + Am[i,j]
      # sum.u[j] <- sum.u[j] + Au[i,j]
      
      # Calculate the products of the rows
      mult.l[i] = mult.l[i] * (Al[i,j])^(1/nrow(Al))
      mult.m[i] = mult.m[i] * (Am[i,j])^(1/nrow(Am))
      mult.u[i] = mult.u[i] * (Au[i,j])^(1/nrow(Au))
    }
  }
  
  
  # Calculate the fuzzy weights - Logarithmic Least Squares (Geometric Mean)  
  for(i in seq(1, length(wl))){
    wl[i] <- mult.l[i]/sum(mult.u)
    wm[i] <- mult.m[i]/sum(mult.m)
    wu[i] <- mult.u[i]/sum(mult.l)
  }
  
  # Add the weights to a data frame
  weights.mat <- cbind(wl, wm, wu)
  
  # Defuzzification using centroid (mean)
  crisp.weights <- numeric(nrow(weights.mat))
  for(i in seq(1, nrow(weights.mat))){
    crisp.weights[i] <- mean(weights.mat[i,])
  }
  
  # Check theis sum
  sum(crisp.weights)
  
  ### Optimization step
  # Create the constraints
  if(sum(crisp.weights) > 1){
    # Minimization - w1+w2+..+wn = 1
    A <- matrix(0, ncol = length(crisp.weights), nrow = 2 * length(crisp.weights)+1 )
    b <- numeric(2 * length(crisp.weights) + 1)
    
    # General constraint
    A[1,] <- rep(1, length(crisp.weights))
    b[1] <- 1
    
    # Specific constrianits
    i <- 2
    for(j in seq(1,ncol(A))){
      A[i,j] <- 1
      b[i] <- 0.9 * crisp.weights[j]
      i <- i+1
      A[i,j] <- -1
      b[i] <- - 1.1 * crisp.weights[j]
      i <- i+1
    }
    
    # Optimize the weights
    opt <- constrOptim(crisp.weights,sum,NULL,A,b)
    
    # Keep the optimal weights
    opt.crisp.weights <- opt$par
  }else{
    print("lower")
    # Maximization - w1+w2+..+wn = 1
    A <- matrix(0, ncol = length(crisp.weights), nrow = 2 * length(crisp.weights)+1 )
    b <- numeric(2 * length(crisp.weights) + 1)
    
    # General constraint
    A[1,] <- rep(-11, length(crisp.weights))
    b[1] <- -1
    
    # Specific constrianits
    i <- 2
    for(j in seq(1,ncol(A))){
      A[i,j] <- 1
      b[i] <- 0.9 * crisp.weights[j]
      i <- i+1
      A[i,j] <- -1
      b[i] <- - 1.1 * crisp.weights[j]
      i <- i+1
    }
    
    # Optimize the weights
    opt <- constrOptim(crisp.weights,sum,NULL,A,b, control = list(fnscale =-1 ))
    
    # Keep the optimal weights
    opt.crisp.weights <- opt$par

  }
  
  # If optimization fails to converge ...!!!!
  # Just normalize the weights...!!!
  if(sum(opt.crisp.weights )> 1.01 | sum(opt.crisp.weights )< 0.99){
    print("Not one!!!")
    opt.crisp.weights = crisp.weights/sum(crisp.weights)
  }
  
  
  # Add them to a list
  if(first){
    l1 <- list(crisp.weights)
    l2 <- list(opt.crisp.weights)
    l3 <- list(( opt.crisp.weights - crisp.weights)/crisp.weights)
    n <- names(df)
    char.names <- list(n[[1]])
    first <- FALSE
  }else{
    l1 <- c(l1, list(crisp.weights))
    l2 <- c(l2, list(opt.crisp.weights))
    l3 <- c(l3, list((opt.crisp.weights - crisp.weights)/crisp.weights))
    n <- names(df)
    char.names <- c( char.names , n[[1]])
  }
}
# Set the name of each characteristic
names(l1) <- char.names
names(l2) <- char.names
names(l3) <- char.names

# Store the results to a json file
library("jsonlite")
json1 <- toJSON(l1)
json2 <- toJSON(l2)
json3 <- toJSON(l3)
#print("exporting the results")
#print(json2)
setwd(Dir)
setwd("./R_Working_Directory")
# write(json1, "./fuzzy_weights.json")
write(json2, "./weights.json")
# write(json3, "./differences")
#print("results exported")