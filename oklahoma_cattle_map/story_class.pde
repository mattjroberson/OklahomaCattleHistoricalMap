class Story{
  
  PImage storyImage;
  PImage storyImageHighlight;
  boolean hovered;
  private boolean clicked;
  
  PImage storyChartImage;
  Chart chart;
  
  String name;
  String link;
  
  int x, y, w, h;
  
  public Story(String name, String link, int x, int y){
    this.name = name;
    this.link = link;
    
    storyImage = loadImage("story_image.png");
    storyImageHighlight = loadImage("story_image_highlight.png");
    storyChartImage = loadImage(name + ".png");
        
    this.x = x;
    this.y = y;
    
    w = 25;
    h = 29;
    
    clicked = false;
  }
  
  public Story(String name, String link){
    this(name, link, 0,0);
  }
  
  public void attachChart(Chart chart, boolean first){
    this.chart = chart;
    if(first){
    x += chart.county.x + 1;
    y += chart.county.y + 1;
    }
    else{
      x += chart.county.x + chart.county.countyImage.width - w - 1;
      y += chart.county.y + chart.county.countyImage.height - h - 1;
    }
  }
  
  boolean mousePressed(){
    if(hovered){
      clicked = true;
      return true;
    }
    else return false;
  }
  
  void stopClicked(){
    clicked = false;
  }
  
  void select(){
    clicked = true;
  }
  
  void drawStory(float x, float y){
    image(storyChartImage, x, y);
  }
  
  void render(){
    hovered = mouseBounds(x, y, w, h);
    
    if(hovered || clicked){
      image(storyImageHighlight, x, y);
    }
    else{
      image(storyImage, x, y);
    }
  }
}
