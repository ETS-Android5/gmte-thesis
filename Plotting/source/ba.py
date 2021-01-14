import os, csv, statistics
import matplotlib.pyplot as plt

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
path = os.path.join(root, "../data/BA.csv")

# read the csv file, converting the data to an array of floats per condition
data = []
with open(path) as csvfile:
    lines = csv.reader(csvfile, quoting=csv.QUOTE_NONNUMERIC)
    for line in lines:
        data.append(line)

# make labels and a color list for each of the conditions
labels = ["APP", "VTF", "VDF", "VPF"]
condcolors = ["tab:brown", "tab:purple", "tab:green", "tab:cyan"] # tab:gray for VCF

# create subplots to arrange box plot
fig, ax = plt.subplots(figsize=(7.5, 5))

# --- box plot ---
# generate boxplot and set colors
i = 0
boxplot = ax.boxplot(data, labels=labels, patch_artist=True)
for patch in boxplot["boxes"]:
    patch.set_facecolor(condcolors[i])
    patch.set_edgecolor(condcolors[i])
    i += 1

# annotate boxplot with significance lines
annotate_significance(ax, 1, 2, 10 + (10 * 0.05), "**")
annotate_significance(ax, 2, 3, 10 + (10 * 0.05), "*")

annotate_significance(ax, 1, 3, 10 + (10 * 0.25), "**")
annotate_significance(ax, 2, 4, 10 + (10 * 0.15), "**")

annotate_significance(ax, 1, 4, 10 + (10 * 0.35), "*")

# global plot settings
ax.yaxis.grid(True, alpha=0.5)
ax.set_axisbelow(True)

ax.set_yticks(range(0, 10 + 1, 1))
ax.set_ylabel("Balance Awareness", weight="bold")
ax.set_xlabel("Feedback Solution", weight="bold")

# save and show the plot!
name = "BA.png"
plt.savefig("C:/Users/Mark Berentsen/Documents/Programming/GMTE Thesis/Plotting/images/" + name, bbox_inches="tight")
plt.show()