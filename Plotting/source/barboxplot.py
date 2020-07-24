import os, csv, statistics
import matplotlib.pyplot as plt

def annotate_significance(ax, start, stop, height, significance):
    '''
    draws an annotation with a bar between two conditions to show significance
        :ax           - the ax to draw on
        :start        - the start condition
        :stop         - the stop condition
        :height       - the height to draw the bar at
        :significance - the significance between the conditions, in *s
    '''

    # setup the main variables
    x1, x2 = start, stop
    y, h, c = height, 2, 'k'

    # draw the line and the text
    ax.plot([x1, x1, x2, x2], [y, y + h, y + h, y], linewidth=1, color=c)
    ax.annotate(significance, ((x1 + x2) * 0.5, y + h), horizontalalignment="center", verticalalignment="bottom", color=c)

# get the path to the csv file
root = os.path.dirname(__file__)
path = os.path.join(root, "../data/survey-UMUX.csv")

# read the csv file, converting the data to an array of floats per condition
data = []
with open(path) as csvfile:
    lines = csv.reader(csvfile, quoting=csv.QUOTE_NONNUMERIC)
    for line in lines:
        data.append(line)
labels = ["Vibrotactile", "Color Wheel", "Directional", "Application"]

# create subplots to arrange bar and box plots
fig, axes = plt.subplots(ncols=2, figsize=(15, 5))

# --- bar plot ---
# calculate the means and standard deviations for the bar plot
means, stdevs = [], []
for condition in data:
    means.append(statistics.mean(condition))
    stdevs.append(statistics.stdev(condition))

# generate barplot and annotate error bars
barplot = axes[0].bar(labels, means, yerr=stdevs, capsize=10, color='c', edgecolor='k')
for i in range(4):
    axes[0].annotate(round(stdevs[i], 2), (labels[i], means[i] + stdevs[i]), xytext=(5, -15), textcoords="offset points")

# --- box plot ---
# generate boxplot and set colors
boxplot = axes[1].boxplot(data, labels=labels, patch_artist=True)
for patch in boxplot["boxes"]:
    patch.set_facecolor('c')
    patch.set_edgecolor('k')

# annotate boxplot with significance lines
annotate_significance(axes[1], 1, 2, 105, "***")  #0.0004p = ***
annotate_significance(axes[1], 3, 4, 105, "****") #0.0001p = ****
annotate_significance(axes[1], 2, 4, 115, "****") #0.0000p = ****


# global plot settings
for ax in axes:
    ax.yaxis.grid(True, alpha=0.5)
    ax.set_axisbelow(True)
    
    ax.set_yticks(range(0, 100 + 1, 10))
    ax.set_ylabel("UMUX Score", weight="bold")
    ax.set_xlabel("Feedback Solution", weight="bold")

# show the plot!
plt.show()