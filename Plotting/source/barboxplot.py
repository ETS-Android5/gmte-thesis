import os, csv

# get the path to the preferred csv file
root = os.path.dirname(__file__)
path = os.path.join(root, "../data/survey-UMUX.csv")

# read the csv file, converting the data to an arrays of floats per condition
data = []
with open(path) as csvfile:
    lines = csv.reader(csvfile, quoting=csv.QUOTE_NONNUMERIC)
    for line in lines:
        data.append(line)

print(data)