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
path = os.path.join(root, "../data/TLX.csv")

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
submeans = np.array_split(allmeans, 7)
substdevs = np.array_split(allstdevs, 7)

# sort the arrays to be grouped per condition
means = [list(x) for x in zip(*submeans)]
stdevs = [list(x) for x in zip(*substdevs)]

# make labels, a color list and legend for each of the conditions
labels = ["TLX", "Mental Demand", "Physical Demand", "Temporal Demand", "Performance", "Effort", "Frustration"]
condcolors = ["tab:brown", "tab:purple", "tab:green", "tab:cyan"] # tab:gray for VCF
legend = ["APP", "VTF", "VDF", "VPF"]

# label locations, bar widths and bar positions
x = np.arange(len(labels))
barwidth = 0.2
barpos = [x - (barwidth + barwidth / 2), x - barwidth / 2, x + barwidth / 2, x + (barwidth + barwidth / 2)]

# create subplots to arrange bar plot
fig, ax = plt.subplots(figsize=(15, 5))

# annotate barplot with significance lines 
maxmeans = []
for i in range(7):
    maxmeans.append(max(submeans[i]) + substdevs[i][np.argmax(submeans[i])])

annotate_significance(ax, barpos[1][0], barpos[3][0], maxmeans[0] + (maxmeans[0] * 0.05), "**")
annotate_significance(ax, barpos[0][0], barpos[3][0], maxmeans[0] + (maxmeans[0] * 0.15), "*")
annotate_significance(ax, barpos[2][1], barpos[3][1], maxmeans[1] + (maxmeans[1] * 0.05), "**") 
annotate_significance(ax, barpos[1][1], barpos[3][1], maxmeans[1] + (maxmeans[1] * 0.15), "**") 
annotate_significance(ax, barpos[0][1], barpos[3][1], maxmeans[1] + (maxmeans[1] * 0.25), "**") 
annotate_significance(ax, barpos[1][4], barpos[3][4], maxmeans[4] + (maxmeans[4] * 0.05), "**") 
annotate_significance(ax, barpos[1][6], barpos[2][6], maxmeans[6] + (maxmeans[6] * 0.05), "*") 
annotate_significance(ax, barpos[1][6], barpos[3][6], maxmeans[6] + (maxmeans[6] * 0.15), "**") 
annotate_significance(ax, barpos[0][6], barpos[3][6], maxmeans[6] + (maxmeans[6] * 0.25), "**") 
 
# generate barplots
for i in range(4):
    ax.bar(barpos[i], means[i], barwidth, yerr=stdevs[i], capsize=2.5, color=condcolors[i], edgecolor=condcolors[0], label=legend[i])

# global plot settings
ax.yaxis.grid(True, alpha=0.5)
ax.set_axisbelow(True)

ax.set_ylim(ymin=0)
ax.set_yticks(range(0, 100 + 1, 10))
ax.set_ylabel("NASA TLX Score", weight="bold")

ax.set_xticks(x)
ax.set_xticklabels(labels)
ax.set_xlabel("NASA TLX Scales", weight="bold")

ax.legend(loc=9)

# save and show the plot!
name = "TLX.png"
plt.savefig("C:/Users/Mark Berentsen/Documents/Programming/GMTE Thesis/Plotting/images/" + name, bbox_inches="tight")
plt.show()