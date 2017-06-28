## What this is about

Documentation describes software usually from a static perspective. **But what if we could get a human-readable and understandable documentation for a specific run of a software?** Currently, we have to investigate runtime details in complex views of profilers and debuggers. With our Method Execution Reports we now provide a **short and understandable summary of a program run from the perspective of a single method of interest**. We automatically create the interactive web-based documents that merge highly adaptive texts and graphics. They summarize data on call structures, recursion, and performance. The reports answer many questions about the runtime behavior of the method and might act as a better starting point for a detailed analysis using profilers and debuggers.

Our implementation is a **proof of concept** that demonstrates the idea of runtime reports **for Java methods** - we invite everybody to pick up our ideas and implement similar reports for extended scenarios. To learn more, please have a look at our VISSOFT 2017 paper that describes the scientific background (see below).

## See an example

<img alt="Method Execution Report for method paintEntries" src="images/paintentries.png" width="500">

The example shows a screenshot of Java method *paintEntries*. It is a recursive method that draws a [treemap](https://en.wikipedia.org/wiki/Treemapping) on a panel to visualize a hierarchy. Try out the [interactive example](examples/paintEntries/paintEntries.html).

A [second example](examples/computeCentroids/computeCentroids.html) describes Java method *computeCentroids*, which is executed as part of a [k-means clustering](https://en.wikipedia.org/wiki/K-means_clustering) run.

## Create your own reports

coming soon ...

## Learn more

We published a paper at VISSOFT 2017 that describe the details of our approach. As supplemental material to that, we also provide a detailed specification of the report generation and results from a small user study that we performed to get developers' feedback on our idea.

### Publication

**Abstract:** To obtain an accurate understanding of a program behavior, developers use a set of tools and techniques such as logging outputs, debuggers, profilers, and visualizations. These support an in-depth analysis of the program behavior, each approach focusing on a different aspect. What is missing, however, is an approach to get an overview of a program execution. As a first step to fill this gap, this paper presents an approach to generate Method Execution Reports. Each report summarizes the execution of a selected method for a specific execution of the program using natural-language text and embedded visualizations. A report provides an overview of the dynamic calls and time consumption related to the selected method. We present a framework to generate these reports and discuss the specific instantiation and phrasing we have chosen. Our results comprise feedback from developers discussing the understandability and usefulness of our approach and a task-based comparison to state-of-the-art solutions.

**Reference:** Beck, Fabian; Siddiqui, Hafiz Ammar; Bergel, Alexandre; Weiskopf, Daniel: Method Execution Reports: Generating Text and Visualization to Describe Program Behavior. In: Proceedings of the 5th IEEE Working Conference on Software Visualization. IEEE, 2017.

**Paper PDF:** coming soon...

### Report specification

The reports are based on decision graphs that determine the content of the reports and connected text templates that define the exact phrasing. As a documentation of this, we provide graphs and templates in [specification document](docs/report_specification.pdf) - the paper (see above) gives an explanation how to read this specification.

### User study


