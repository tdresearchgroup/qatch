##*************** Threshold Extractor *****************************
#
#  This script is responsible for the calculation of the thresholds
#  of the Quality Model's properties.
#
#
#*******************************************************************



# Set the working directory to the appropriate path

args <- commandArgs(trailingOnly = TRUE)


Dir <- args[1]

setwd(Dir)


# Load the appropriate libraries
library("xlsx")

# Read the data frame
df <- read.xlsx("properties.xls", sheetIndex = 1, header = TRUE)

#Create an empty vector
first <- TRUE
# Iterate through the properties
for(i in 1:ncol(df)){
  #Get the current column (i.e property)
  property <- df[[i]]
  
  #Calculate the middle threshold
  t2 <- median(property)
  
  #Calculate the lower threshold (the minimum non-outlier observation)
  threshold <- quantile(property, c(0.25)) - 1.5 * IQR(property)
  index <- (property >= threshold)
  part <- property[index]
  t1 <- min(part)
  
  #Calculate the upper threshold (the maximum non-outlier observation)
  threshold <- quantile(property, c(0.75)) + 1.5 * IQR(property)
  index <- (property <= threshold)
  part <- property[index]
  t3 <- max(part)
  
  #Add a new row to the exported data frame
  if(first == TRUE){
    t <- c(t1, t2, t3)
    first <- FALSE
  }else{
    t <- rbind(t, c(t1,t2,t3))
  }
  
}

# Create the dataframe with the thresholds
rownames <- names(df)
colnames <- list("t1","t2","t3")
thresholds <- data.frame(t, row.names = rownames)
names(thresholds) <- colnames



library("jsonlite")
json <- toJSON(thresholds)
write(json, "threshold.json")

# Export the results in csv and json format
# write.xlsx(thresholds,"thresholds.xls")