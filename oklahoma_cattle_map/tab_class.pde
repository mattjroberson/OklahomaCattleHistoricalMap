class ChartTab {
  private PImage tabSelected;
  private PImage tabHighlighted;
  private PImage tabDark;
  private PImage tabCurrent;
  
  private float x;
  private float y;
  private float w;
  private float h;
  private float yTop;
  private float yMid;
  private float yMid2;
  private float yBot;
  
  boolean clicked;
  boolean hovered;
  boolean isStoryTab;
  
  ChartType type;
  Story story;
  
  String name;
      
  public ChartTab(ChartType type, float x, float[] yVals, float w, float h){
    this.type = type;
    name = getNameFromType();
    
    this.x = x;
    this.w = w;
    this.h = h;
    
    yTop = yVals[0];
    yMid = yVals[1];
    
    if(type == ChartType.STORY) {
      yMid2 = yVals[2];
      yBot = yVals[3];
    }
    else {
      yBot = yVals[2];
    }
    
    setYValue();

    clicked = false;
    hovered = false;
    isStoryTab = false;
    
    loadImages();
  }
  
  public ChartTab(Story story, float x, float[] yVals, float w, float h){
    this(ChartType.STORY, x, yVals, w, h);
    this.story = story;
    isStoryTab = true;
  }
  
  private void setYValue(){
     switch (type) {
       case TOTALS:
           if(chartType == ChartType.TOTALS) y = yTop;
           else if(chartType == ChartType.KEY) y = yMid;
           else y = yBot;
           break;
       case PERCENTAGE:
           if(chartType == ChartType.PERCENTAGE) y = yTop;
           else if(chartType == ChartType.KEY) y = yMid;
           else y = yBot;
           break;
       case KEY:
           if(chartType == ChartType.KEY) y = yTop;
           else y = yBot;
           break;
       case STORY:
           if(chartType == ChartType.STORY)  y = yTop;
           else if(chartType == ChartType.KEY) y = yMid;
           else y = yBot;
     }
  }
  
  private String getNameFromType(){
    switch (type) {
      case TOTALS: return "total";
      case PERCENTAGE: return "percent";
      case KEY: return "key";
      case STORY: return "story";
      default: return "na";
    }
  }
  
  private void loadImages(){
    tabSelected = loadImage("tab_" + name + "_selected.png");
    tabHighlighted = loadImage("tab_" + name + "_highlight.png");
    tabDark = loadImage("tab_" + name + "_dark.png");
    tabCurrent = (chartType == type) ? tabSelected : tabDark;
  }
  
  public boolean mousePressed(){
    if(hovered == true){
      clicked = true;
      return true;
    }
    return false;
  }

  public void setY(String position){    
    switch(position) {
      case "top": 
        y = yTop;
        break;
      case "middle":
        y = yMid;
        break;
      case "middle 2":
         y = yMid2;
         break;
      case "bottom":
        y= yBot;
        break;
    }
  }
  
  public void select(){
    tabCurrent = tabSelected;
    clicked = true;
  }
  
  public void checkHovered(){
    if(mouseBounds(x, y, w, h) == true){
      if(!clicked) tabCurrent = tabHighlighted;
      hovered = true;
    }
    else{
      if(!clicked) tabCurrent = tabDark;
      hovered = false;
    }
  }
  
  public void stopClicked(){
    clicked = false;
  }
  
  public void render(){
    image(tabCurrent, x, y);
  }
}
