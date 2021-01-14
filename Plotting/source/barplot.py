import os, csv, statistics
import matplotlib.pyplot as plt
import numpy as np

def annotate_significance(ax, start, stop, height, significance):
    '''
    draws an annotation with a bar between two conditions to show significance
        :ax           - the ax to draw on
        :start        - the start condition
        :stop         - the stop condition
        :height       - the height to draw the bar at
        :significance - the significance between the conditions, in *s (string)
    '''

    # setup the main variables
    x1, x2 = start, stop
    y, h, c = height, height * 0.02, 'k'

    # draw the line and the text
    ax.plot([x1, x1, x2, x2], [y, y + h, y + h, y], linewidth=1, color=c)
    ax.annotate(significance, ((x1 + x2) * 0.5, y + h), horizontalalignment="center", verticalalignment="bottom", color=c)

# get the path to the csv file
root = os.path.dirname(__file__)
path = os.path.join(root, "../data/DVs.csv")

# read the csv file, converting the data to an array of floats per condition
data = []
with open(path) as csvfile:
    lines = csv.reader(csvfile, quoting=csv.QUOTE_NONNUMERIC)
    for line in lines:
        data.append(line)

# calculate the means and standard deviations for the bar plots
allmeans, allstdevs = [], []
for condition in data:
    allmeans.append(statistics.mean(condition))
    allstdevs.append(statistics.stdev(condition))

# split the mean and stdev arrays
means = np.array_split(allmeans, 4)
stdevs = np.array_split(allstdevs, 4)

# make labels and a color list for each of the conditions
labels = ["APP", "VTF", "VDF", "VPF"]
condcolors = ["tab:brown", "tab:purple", "tab:green", "tab:cyan"] # tab:gray for VCF

# make plot labels
ylabels = ["Balance Performance (%)", "Balance Deviation (cm)", "Response Time (ms)", "Completion Time (s)"]

# create subplots to arrange bar and box plots
fig, axes = plt.subplots(nrows=2, ncols=2, figsize=(15, 10))

# annotate boxplot with significance lines 
maxmeans = []
for i in range(4):
    maxmeans.append(max(means[i]) + stdevs[i][np.argmax(means[i])])

annotate_significance(axes[0, 0], 1, 3, maxmeans[0] + (maxmeans[0] * 0.05), "**") 
annotate_significance(axes[0, 1], 2, 3, maxmeans[1] + (maxmeans[1] * 0.05), "**")  
annotate_significance(axes[0, 1], 1, 3, maxmeans[1] + (maxmeans[1] * 0.15), "**")  
annotate_significance(axes[0, 1], 0, 3, maxmeans[1] + (maxmeans[1] * 0.25), "**")  
annotate_significance(axes[1, 0], 1, 3, maxmeans[2] + (maxmeans[2] * 0.05), "*")  

# generate barplots
l = 0
for x in range(2):
    for y in range(2):
        barplot = axes[x, y].bar(labels, means[l], yerr=stdevs[l], capsize=10, color=condcolors, edgecolor=condcolors)
        for i in range(4):
            axes[x, y].annotate(round(stdevs[l][i], 2), (labels[i], means[l][i] + stdevs[l][i]), xytext=(5, -15), textcoords="offset points")

        # global plot settings
        axes[x, y].yaxis.grid(True, alpha=0.5)
        axes[x, y].set_axisbelow(True)
        
        axes[x, y].set_ylim(ymin=0)
        axes[x, y].set_ylabel(ylabels[l], weight="bold")
        axes[x, y].set_xlabel("Feedback Solution", weight="bold")
        
        l += 1

# save and show the plot!
name = "DVs.png"
plt.savefig("C:/Users/Mark Berentsen/Documents/Programming/GMTE Thesis/Plotting/images/" + name, bbox_inches="tight")
plt.show()