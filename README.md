## What this is about

Documentation describes software usually from a static perspective. **But what if we could get a human-readable and understandable documentation for a specific run of a software?** Currently, we have to investigate runtime details in complex views of profilers and debuggers. With our Method Execution Reports we now provide a **short and understandable summary of a program run from the perspective of a single method of interest**. We automatically create the interactive web-based documents that merge highly adaptive texts and graphics. They summarize data on call structures, recursion, and performance. The reports answer many questions about the runtime behavior of the method and might act as a better starting point for a detailed analysis using profilers and debuggers.

Our implementation is a **proof of concept** that demonstrates the idea of runtime reports **for Java methods** - we invite everybody to pick up our ideas and implement similar reports for extended scenarios. To learn more, please have a look at our VISSOFT 2017 paper that describes the scientific background (see below).

## See an example

The example below shows a screenshot of Java method *paintEntries*. It is a recursive method that draws a [treemap](https://en.wikipedia.org/wiki/Treemapping) on a panel to visualize a hierarchy.

![Method Execution Report for method paintEntries](https://fabian-beck.github.io/Method-Execution-Reports/images/paintentries.png)

## Create your own reports

## Learn more

### Publication

### User Study
