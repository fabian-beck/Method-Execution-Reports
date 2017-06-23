      // Variables
      var toggleOver = true;
      var toggleDOD = true;
      var oldVisID = "";
      var hover = "hover";
      var nohover = "nohover";
      var whiteColor = "#FFFFFF";
      var backgroundColor = "#DDDDDD";
      var overColor = "#BBBBBB"
      var progressBarVisPOC = "progressBarVisPOC";
      var noPOC = "noPOC";
      var visWidth = 32;
      var visHeight = 14;
      var vis1Width = 64;
      var vis1Height = 28;
      var vis2Width = 256;
      var vis2Height = 112;
      var vis22Width = 550;
      var vis22Height = 300;
      var niceIntervals = [1, 2, 3, 4, 5, 6, 7, 8, 9];
      var niceIntervalsNew = [1, 2, 5];
      var barChartVisRangeValues = 50;
      var scrollTimer;
      var popupsContent;
      var codeVisible = false;

      // Deep Sky Blue, Lime Green, Gold, Orange Peel, Heliotrope, Folly, Carnation Pink
      var progressBarVisColors = ["#00BFFF","#32CD32","#FFD700","#FF9F00","#DF73FF","#FF004F","#FFA6C9"];

      // Calculate progress bar fill width
      function getProgressBarVisFillWidth(d)
      {
        var percentFraction = (progressBarVisValues[d] / progressBarVisTotal[d]);
        return (percentFraction*(visWidth-1))+0.5;
      }

      // Get progress bar fill color
      function getProgressBarVisFill(d)
      {
        return progressBarVisFills[d];
      }

      // Get progress bar ID
      function getProgressBarVisID(d)
      {
        return "pbv"+d;
      }

      // Calculate progress bar percentage
      function getProgressBarVisPercent(d)
      {
        var percent = (progressBarVisValues[d] / progressBarVisTotal[d]) * 100;
        percent = +percent.toFixed(1);
        return percent+"%";
      }

      // Trigger when mouse enters progress bar
      function progressBarVisMouseEnter(d)
      {
        if(toggleOver)
        {
          if(oldVisID != "")
          {
            d3.select("#"+oldVisID).style("background-color", backgroundColor);
          }
          var pbvID = getProgressBarVisID(d);
          oldVisID = getProgressBarVisID(d);
          d3.select("#"+pbvID).style("background-color", overColor);
          $('#'+getProgressBarVisID(d)+'v').popover("show");
        }
      }

      // Trigger when mouse leaves progress bar
      function progressBarVisMouseLeave(d)
      {
        if(toggleOver)
        {
          var pbvID = getProgressBarVisID(d);
          d3.select("#"+pbvID).style("background-color", backgroundColor);
          $('#'+getProgressBarVisID(d)+'v').popover("hide");
        }
      }

      function progressBarVis1Placement(d)
      {
          if(d >= raPBVisIDs)
          {
              return 'auto right';
          }
          else
          {
              return 'auto top';
          }
      }

      // Hover popover content for progress bar
      function progressBarVis1Content(d)
      {
        var content = "<div style=\"text-align:center\">"
                    + "<svg class=\"progressBarVis1Container\">"
                    + "<g>"
                    + "<rect class=\"progressBarVis1Empty\" width=\""+vis1Width+"\" height=\""+vis1Height+"\"></rect>"
                    + "<rect class=\"progressBarVis1Fill\" width=\""+getProgressBarVis1FillWidth(d)+"\" height=\""+vis1Height+"\" style=\"fill: "+getProgressBarVisFill(d)+";\"></rect>"
                    + "<rect class=\"progressBarVis1Border\" width=\""+vis1Width+"\" height=\""+vis1Height+"\"></rect>";
                    + "</g>"
                    + "</svg>";

        var percent = (progressBarVisValues[d] / progressBarVisTotal[d]) * 100;
        percent = +percent.toFixed(2);

        content = content
                + "<text class=\"progressBarVis1Text\" x=\""+((vis1Width/2)+1)+"\" y=\""+((vis1Height/2)+1)+"\" alignment-baseline=\"middle\" text-anchor=\"middle\">"+percent+"%</text>"
                + "</g>"
                + "</svg>";

        content = content
                + "</div>"
                + "<div class=\"progressBarVis1MainLabel\">"+ progressBarVisValues[d] + " out of " + progressBarVisTotal[d]+"</div>"

        return content;
      }

      // Calculate progress bar fill width for popover
      function getProgressBarVis1FillWidth(d)
      {
        var percentFraction = (progressBarVisValues[d] / progressBarVisTotal[d]);
        return (percentFraction*(vis1Width-1))+0.5;
      }

      // Get bar chart ID
      function getBarChartVisID(d)
      {
        return "bcv"+d;
      }

      // Get bar chart popup ID
      function getBarChartVisPopupID(d)
      {
        return "bcvp"+d;
      }

      // Get bar chart popup content
      function getBarChartVisPopupContentID(d)
      {
        return "bcvpc"+d;
      }

      // Trigger when mouse enters bar chart
      function barChartVisMouseEnter(d)
      {
        if(toggleOver == true)
        {
          if(oldVisID != "")
          {
            d3.select("#"+oldVisID).style("background-color", backgroundColor);
          }

          var bcvID = getBarChartVisID(d);
          oldVisID = getBarChartVisID(d);
          d3.select("#"+bcvID).style("background-color", overColor);
          d3.select("#"+bcvID).style("cursor", "pointer");
          $('#'+getBarChartVisID(d)+'v').popover("show");
        }
      }

      // Trigger when mouse leaves bar chart
      function barChartVisMouseLeave(d)
      {
        if(toggleOver == true)
        {
          var bcvID = getBarChartVisID(d);
          d3.select("#"+bcvID).style("background-color", backgroundColor);
          d3.select("#"+bcvID).style("cursor", "auto");
          $('#'+getBarChartVisID(d)+'v').popover("hide");
        }
      }

      // Trigger when mouse clicks bar chart
      function barChartVisMouseClick(d)
      {
        if(toggleOver == true)
        {
          var bcvID = getBarChartVisID(d);
          $('.popover').remove();
        }
      }

      // Hover popover content for bar chart
      function barChartVis2Content(d)
      {
        var content = "<div style=\"text-align:center\">"
                    + "<svg class=\"barChartVis2Container\">"
                    + "<g class=\"barChartVis2ContainerGroup\">"
                    + "<rect class=\"barChartVis2Empty\" width=\""+vis2Width+"\" height=\""+vis2Height+"\"></rect>";

        var totalValues = barChartVisValues[d].length;
        var maxValue = 0;
        var moreDetails;
        if(totalValues > barChartVisRangeValues)
        {
          totalValues = barChartVisRangeValues;
          maxValue = Math.max.apply(Math, barChartVisValues[d].slice(0, barChartVisRangeValues));
          moreDetails = "Showing first "+barChartVisRangeValues+" values, click for full details<span class=\"glyphicon glyphicon-chevron-right\"/>";
        }
        else
        {
          maxValue = Math.max.apply(Math, barChartVisValues[d]);
          moreDetails = "Click for details";
        }
        var barWidth = (vis2Width-1) / totalValues;

        for(var i=0; i<totalValues; i++)
        {
          content = content
                  + "<rect class=\"barChartVis2Fill\" width=\""+barWidth+"\" height=\""+((barChartVisValues[d][i]/maxValue)*vis2Height)+"\" x=\""+((i*barWidth)+0.5)+"\" y=\""+(((vis2Height-1) - ((barChartVisValues[d][i]/maxValue)*vis2Height))+0.5)+"\" style=\"fill: "+getBarChartVisFill(d,i)+";\"></rect>";
        }

        content = content
                + "<rect class=\"barChartVis2Border\" width=\""+vis2Width+"\" height=\""+vis2Height+"\"></rect>"
                + "</g>"
                + "</svg>";

        content = content
                + "</div>"
                + "<div class=\"barChartVis2MainLabel\">"+barChartVisValueMainLabels[d]+"</div>"

        content = content
                + "<div class=\"moreDetails\">"+moreDetails+"</div>"

        return content;
      }

      // Get bar chart fill color
      function getBarChartVisFill(d,e)
      {
        if(e%2 == 0)
        {
          return barChartVisFillsEven[d];
        }
        else
        {
          return barChartVisFillsOdd[d];
        }
      }

      // Get highlight popover ID
      function getHighlightPopoverVisID(d)
      {
        return "hpv"+d;
      }

      // Hover popover content for highlight popover
      function highlightPopoverVis3Content(d)
      {
        return highlightPopoverVisValues[d];
      }

      // Trigger when mouse enters highlight popover
      function highlightPopoverVisMouseEnter(d)
      {
        if(toggleOver == true)
        {
          var hpvID = getHighlightPopoverVisID(d);
          d3.select("#"+hpvID).style("background-color", overColor);
        }
      }

      // Trigger when mouse leaves highlight popover
      function highlightPopoverVisMouseLeave(d)
      {
        if(toggleOver == true)
        {
          var hpvID = getHighlightPopoverVisID(d);
          d3.select("#"+hpvID).style("background-color", backgroundColor);
        }
      }

      // Get highlight popup ID
      function getHighlightPopupVisID(d)
      {
        return "huv"+d;
      }

      // Get highlight popup popup ID
      function getHighlightPopupVisPopupID(d)
      {
        return "huvp"+d;
      }

      // Trigger when mouse enters highlight popup
      function highlightPopupVisMouseEnter(d)
      {
        if(toggleOver == true)
        {
          var huvID = getHighlightPopupVisID(d);
          d3.select("#"+huvID).style("background-color", overColor);
          d3.select("#"+huvID).style("cursor", "pointer");
        }
      }

      // Trigger when mouse leaves highlight popup
      function highlightPopupVisMouseLeave(d)
      {
        if(toggleOver == true)
        {
          var huvID = getHighlightPopupVisID(d);
          d3.select("#"+huvID).style("background-color", backgroundColor);
          d3.select("#"+huvID).style("cursor", "auto");
        }
      }

      // Trigger when mouse clicks highlight popup highlightPopupVisMouseClick
      function highlightPopupVisMouseClick(d)
      {
        if(toggleOver == true)
        {
          var huvID = getHighlightPopupVisID(d);
          $('.popover').remove();
        }
      }

      // Calculate the intervals gap
      function getIntervalGap(max, no)
      {
        var gapFound = false;
        for(var i=1; gapFound == false; i = i*10)
        {
          for(var j=0; j<niceIntervals.length; j++)
          {
            if((niceIntervals[j] * i * no) >= max)
            {
              gapFound = true;
              return (niceIntervals[j] * i);
            }
          }
        }
      }

      // Get text representation of a number
      function getNoText(no)
      {
        if(no >= 1000000000000)
        {
          return (no / 1000000000000) + "T";
        }
        else if(no >= 1000000000)
        {
          return (no / 1000000000) + "B";
        }
        else if(no >= 1000000)
        {
          return (no / 1000000) + "M";
        }
        else if(no >= 1000)
        {
          return (no / 1000) + "K";
        }
        else
        {
          return no;
        }
      }

      // Get times or time depending upon no
      function getTimes(no)
      {
        if(no == 1)
        {
          return "Time";
        }
        else
        {
          return "Times";
        }
      }

      // Scroll content on right side
      function goRight(containerID)
      {
       $("#"+containerID).animate({ scrollLeft: "+=25"}, 100);
       scrollTimer = setTimeout(function() { goRight(containerID) },100);
      }

      // Scroll content on left side
      function goLeft(containerID)
      {
       $("#"+containerID).animate({ scrollLeft: "-=25"}, 100);
       scrollTimer = setTimeout(function() { goLeft(containerID) },100);
      }

      // Stop scrolling
      function scrollStop()
      {
        clearTimeout(scrollTimer);
      }

      // Popups for highlight
      for(var i=0; i<highlightPopupVisIDs.length; i++)
      {
        popupsContent = "<div class=\"modal fade\" id=\""+getHighlightPopupVisPopupID(i)+"\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"basicModal\" aria-hidden=\"true\">"
                      + "<div class=\"modal-dialog\">"
                      + "<div class=\"modal-content\">"
                      + "<div class=\"modal-header\">"
                      + "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>"
                      + "<h4 class=\"modal-title\" >"+highlightPopupValueLabels[i]+"</h4>"
                      + "</div>"
                      + "<div class=\"modal-body\">"

       popupsContent = popupsContent
                      + highlightPopupVisValues[i];

       popupsContent = popupsContent
                      + "</div>"
                      + "</div>"
                      + "</div>"
                      + "</div>"

        document.write(popupsContent);
      }

      // Popups for bar chart visualizations
      for(var i=0; i<barChartVisIDs.length; i++)
      {
        popupsContent = "<div class=\"modal fade\" id=\""+getBarChartVisPopupID(i)+"\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"basicModal\" aria-hidden=\"true\">"
                      + "<div class=\"modal-dialog\">"
                      + "<div class=\"modal-content\">"
                      + "<div class=\"modal-header\">"
                      + "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\"><span aria-hidden=\"true\">&times;</span></button>"
                      + "<h4 class=\"modal-title\" >"+barChartVisValueMainLabels[i]+"</h4>"
                      + "</div>"
                      + "<div class=\"modal-body\">"

        var maxValuePopup = Math.max.apply(Math, barChartVisValues[i]);
        var intervals;
        if(maxValuePopup > 6)
        {
          intervals = 4;
        }
        else if(maxValuePopup > 4)
        {
          intervals = 3;
        }
        else if(maxValuePopup > 2)
        {
          intervals = 2;
        }
        else
        {
          intervals = 1;
        }

        var intervalsGap = getIntervalGap(maxValuePopup, intervals);
        maxValuePopup = intervalsGap * intervals;
        var intervalHeight = vis22Height / intervals;

        popupsContent = popupsContent
                      + "<div class=\"barChartVis22Markings\">"
                      + "<svg class=\"barChartVis22MarkingsContainer\">"
                      + "<g class=\"barChartVis22MarkingsGroup\">"
                      + "<rect class=\"barChartVis22MarkingsEmpty\" x=\"0\" y=\"0\"></rect>";

        for(var l=0; l<(intervals); l++)
        {
          popupsContent = popupsContent
                  + "<text class=\"progressBarVis22MarkingsText\" x=\"35\" y=\""+((intervalHeight*l)+10)+"\" alignment-baseline=\"middle\" text-anchor=\"middle\">"+getNoText(intervalsGap*(intervals-l))+"</text>"
        }

        popupsContent = popupsContent
                + "<text class=\"progressBarVis22MarkingsText\" x=\"35\" y=\""+((intervalHeight*intervals)+8)+"\" alignment-baseline=\"middle\" text-anchor=\"middle\">"+getNoText(0)+"</text>"

        popupsContent = popupsContent
                      + "</g>"
                      + "</svg>"
                      + "</div>";

        var containerWidth;
        var overFlowX;
        var barWidthPopup;
        if(barChartVisValues[i].length > 50)
        {
          containerWidth = 11 * barChartVisValues[i].length;
          overFlowX = "auto";
          barWidthPopup = 11;
        }
        else
        {
          containerWidth = vis22Width;
          overFlowX = "hidden";
          barWidthPopup = vis22Width / barChartVisValues[i].length;
        }

        popupsContent = popupsContent
                      + "<div id=\""+getBarChartVisPopupContentID(i)+"\" class=\"barChartVis22Visual\" style=\"overflow-x:"+overFlowX+";\">"
                      + "<svg class=\"barChartVis22VisualContainer\" style=\"width:"+containerWidth+"px;\">"
                      + "<g class=\"barChartVis22VisualGroup\">"
                      + "<rect class=\"barChartVis22Empty\" x=\"0\" y=\"0\" width=\""+containerWidth+"px\" height=\""+vis22Height+"px\"></rect>";


       for(var j=0; j<barChartVisValues[i].length; j++)
       {
         popupsContent = popupsContent
                 + "<rect class=\"barChartVis22Fill\" data-toggle=\"tooltip\" title=\""+barChartVisValueLabels[i][j]+"\" width=\""+barWidthPopup+"\" height=\""+((barChartVisValues[i][j]/maxValuePopup)*vis22Height)+"\" x=\""+((j*barWidthPopup)+0.5)+"\" y=\""+(((vis22Height-1) - ((barChartVisValues[i][j]/maxValuePopup)*vis22Height))+0.5)+"\" style=\"fill: "+getBarChartVisFill(i,j)+";\"></rect>";
       }

       for(var k=0; k<intervals; k++)
       {
         popupsContent = popupsContent
                 + "<line class=\"barChartVis22Line\" x1=\"0\" y1=\""+(intervalHeight*k)+"\" x2=\""+containerWidth+"\" y2=\""+(intervalHeight*k)+"\"/>";
       }

       popupsContent = popupsContent
                      + "</g>"
                      + "</svg>"
                      + "</div>";

       popupsContent = popupsContent
                      + "<div class=\"barChartVis22Arrows\">";

       if(barChartVisValues[i].length>50)
       {
         popupsContent = popupsContent
                       + "<span class=\"glyphicon glyphicon-chevron-left\" onmouseover=\"goLeft('"+getBarChartVisPopupContentID(i)+"')\" onmouseout=\"scrollStop()\" style=\"float:left;\"></span>"
                       + "<span class=\"glyphicon glyphicon-chevron-right\" onmouseover=\"goRight('"+getBarChartVisPopupContentID(i)+"')\" onmouseout=\"scrollStop()\" style=\"float:right;\"></span>";
       }

       popupsContent = popupsContent
                      + "</div>"
                      + "</div>"
                      + "</div>"
                      + "</div>"
                      + "</div>";

        document.write(popupsContent);
      }

      // Find progress bars and bind data to them and add visualization containers
      var progressBarsVis = d3.selectAll(".progressBarVis")
                          .data(progressBarVisIDs)
                          .attr("id", function(d) { return getProgressBarVisID(d); })
                          .style("background-color", function(d) { if(progressBarVisDODs[d]) return backgroundColor; else whiteColor; })
                          .on("mouseenter", function(d) { if(progressBarVisDODs[d]) progressBarVisMouseEnter(d); })
                          .on("mouseleave", function(d) { if(progressBarVisDODs[d]) progressBarVisMouseLeave(d); })
                          .append("span")
                          .attr("id", function(d) { return getProgressBarVisID(d)+"v"; })
                          .attr("data-trigger", 'manual')
                          .attr("data-content", function(d) { return progressBarVis1Content(d); })
                          .attr("data-html", true)
                          .attr("data-placement", function(d) { return progressBarVis1Placement(d); })
                          .append("svg")
                          .attr("class", "progressBarVisContainer")
                          .append("g");

      // Add progress bar empty space in progress bar visualization containers
      progressBarsVis.append("rect")
                   .attr("class", "progressBarVisEmpty")
                   .attr("width", visWidth)
                   .attr("height", visHeight);

      progressBarsVis.append("rect")
                   .attr("class", "progressBarVisFill")
                   .attr("width", function(d) { return getProgressBarVisFillWidth(d); })
                   .attr("height", visHeight)
                   .style("fill", function(d) { return getProgressBarVisFill(d); });


      // Add progress bar border around progress bar visualization containers
      progressBarsVis.append("rect")
                   .attr("class", "progressBarVisBorder")
                   .attr("width", visWidth)
                   .attr("height", visHeight);

      // Add progress bar percentage text in progress bar visualization containers
      progressBarsVis.append("text")
                   .attr("class", "progressBarVisText")
                   .attr("x", visWidth/2)
                   .attr("y", (visHeight/2)+1)
                   .attr("alignment-baseline", "middle")
                   .attr("text-anchor", "middle")
                   .text(function(d) { return getProgressBarVisPercent(d); });

      // Find bar charts and bind data to them and add visualization containers
      var barChartsVis = d3.selectAll(".barChartVis")
                          .data(barChartVisIDs)
                          .attr("id", function(d) { return getBarChartVisID(d); })
                          .attr("data-toggle", "modal")
                          .attr("data-target", function(d) { return "#"+getBarChartVisPopupID(d); })
                          .style("background-color", function(d) { if(barChartVisDODs[d]) return backgroundColor; else whiteColor; })
                          .on("mouseenter", function(d) { if(barChartVisDODs[d]) barChartVisMouseEnter(d); })
                          .on("mouseleave", function(d) { if(barChartVisDODs[d]) barChartVisMouseLeave(d); })
                          .on("click", function(d) { if(barChartVisDODs[d]) barChartVisMouseClick(d); })
                          .append("span")
                          .attr("id", function(d) { return getBarChartVisID(d)+"v"; })
                          .attr("data-trigger", 'manual')
                          .attr("data-content", function(d) { return barChartVis2Content(d); })
                          .attr("data-html", true)
                          .attr("data-placement", 'auto top')
                          .append("svg")
                          .attr("class", "barChartVisContainer")
                          .append("g")
                          .attr("class", "barChartVisContainerGroup");

      // Add bar chart empty space in bar chart visualization containers
      barChartsVis.append("rect")
                    .attr("class", "barChartVisEmpty")
                    .attr("width", visWidth)
                    .attr("height", visHeight);

      // Add bar chart fill spaces in bar chart visualization containers
      for(var i=0; i<barChartVisIDs.length; i++)
      {
        var barChartVisID = getBarChartVisID(i);
        var barChartBarsVis = d3.select("#"+barChartVisID).select(".barChartVisContainer").select(".barChartVisContainerGroup");
        var totalValues = barChartVisValues[i].length;

        var maxValue = 0;
        if(totalValues > barChartVisRangeValues)
        {
          totalValues = barChartVisRangeValues;
          maxValue = Math.max.apply(Math, barChartVisValues[i].slice(0, barChartVisRangeValues));
          d3.select("#"+barChartVisID).append("xhtml:span").attr("class", "glyphicon glyphicon-chevron-right").style("right", "1px").style("top", "5px").style("vertical-align", "top").style("font-size", "10px");
        }
        else
        {
          maxValue = Math.max.apply(Math, barChartVisValues[i]);
        }
        var barWidth = (visWidth-1) / totalValues;

        for(var j=0; j<totalValues; j++)
        {
          barChartBarsVis.append("rect")
                       .attr("class", "barChartVisFill")
                       .attr("width", barWidth)
                       .attr("height", (barChartVisValues[i][j]/maxValue)*visHeight)
                       .attr("x", (j*barWidth)+0.5)
                       .attr("y", ((visHeight-1) - ((barChartVisValues[i][j]/maxValue)*(visHeight))+0.5))
                       .style("fill", function(d) { return getBarChartVisFill(i,j); });
        }
      }

    // Add bar chart border around bar chart visualization container
    barChartsVis.append("rect")
                  .attr("class", "barChartVisBorder")
                  .attr("width", visWidth)
                  .attr("height", visHeight);

    // Find highlight popovers and bind data to them
    var highlightPopoversVis = d3.selectAll(".highlightPopoverVis")
                        .data(highlightPopoverVisIDs)
                        .attr("id", function(d) { return getHighlightPopoverVisID(d); })
                        .attr("data-trigger", hover)
                        .attr("data-content", function(d) { return highlightPopoverVis3Content(d); })
                        .style("background-color", backgroundColor)
                        .on("mouseenter", function(d) { highlightPopoverVisMouseEnter(d); })
                        .on("mouseleave", function(d) { highlightPopoverVisMouseLeave(d); });

    // Find highlight popups and bind data to them
    var highlightPopupsVis = d3.selectAll(".highlightPopupVis")
                        .data(highlightPopupVisIDs)
                        .attr("id", function(d) { return getHighlightPopupVisID(d); })
                        .attr("data-trigger", hover)
                        .attr("data-content", "<div class=\"moreDetails\">Click for details</div>")
                        .attr("data-toggle", "modal")
                        .attr("data-target", function(d) { return "#"+getHighlightPopupVisPopupID(d); })
                        .style("background-color", backgroundColor)
                        .on("mouseenter", function(d) { highlightPopupVisMouseEnter(d); })
                        .on("mouseleave", function(d) { highlightPopupVisMouseLeave(d); })
                        .on("click", function(d) { highlightPopupVisMouseClick(d); });

    // Source code toggle button
    $('#codeBtn').on('click', function(event)
    {
       if(!codeVisible)
       {
          document.getElementById("codeColumn").className = "col-xs-12";
          $("#codeBtn").text('Hide Code');
          codeVisible = true;
       }
       else
       {
          document.getElementById("codeColumn").className = "collapse";
          $("#codeBtn").text('Show Code');
          codeVisible = false;
       }
    });

    // Enbale all hover popovers for visualizations
    $('[data-trigger="hover"]').popover({ html : true, placement: 'auto top' });

    // Enbale all hover tooltips for visualizations
    $('[data-toggle="tooltip"]').tooltip({'container': 'body','placement': 'bottom'});
