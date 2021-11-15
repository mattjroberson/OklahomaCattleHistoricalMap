import java.io.FileNotFoundException;

class County {
  String name;
  String abrev;
  int x;
  int y;
  
  float textX;
  float textY;
  float textSize;
  float textWidth;
  
  int textOffsetX = 0;
  int textOffsetY = 0;
  
  float linePos;
  
  boolean hovered;
  boolean clicked;
  
  Map<String, String> params;

  ArrayList<Float> milkData, beefData, comparedData;
  ArrayList<Integer> yearData;
  ArrayList<Integer> colorValues;

  color red, blue;

  ArrayList<Story> countyStories;
  Chart chart;

  PImage countyImage;
  int firstYear;
  
  PFont regFont;
  PFont boldFont;
  
  int numDivs;
  int divScale;
  int yearIter;

  String prefix;

  County(String name, String abrev, Story[] storyList, int x, int y) {
    this.name = name;
    this.abrev = abrev;
    this.x = x;
    this.y = y;
    
    countyStories = new ArrayList<Story>();
    
    for(int i = 0; i < storyList.length; i++){
      countyStories.add(storyList[i]);
    }
    
    firstYear = 1990;

    yearData = new ArrayList<Integer>();
    comparedData = new ArrayList<Float>();
    params = new LinkedHashMap<String, String>();

    countyImage = loadImage(name+".png");
    hovered = false;
    clicked = false;
    textSize = 15;
    
    regFont = createFont("Arial", textSize);
    boldFont = createFont("Arial Bold", textSize*1.25);

    prefix = "https://quickstats.nass.usda.gov/api/api_GET/?";

    params.put("key", "43D04181-32AD-3085-B721-8B63F6E6DF2F");
    params.put("commodity_desc", "CATTLE");
    params.put("statisticcat_desc", "INVENTORY");
    params.put("short_desc", "CATTLE,%20COWS,%20MILK%20-%20INVENTORY");
    params.put("domain_desc", "TOTAL");
    params.put("state_alpha", "OK");
    params.put("agg_level_desc", "COUNTY");
    params.put("county_name", name);
    params.put("year__GE", String.valueOf(firstYear));
    params.put("source_desc", "SURVEY");
    params.put("format", "JSON");
    params.put("freq_desc", "POINT%20IN%20TIME");

    
    milkData = this.getValues(true);
    Collections.reverse(milkData);
    Collections.sort(yearData);

    params.replace("short_desc", "CATTLE,%20COWS,%20BEEF%20-%20INVENTORY");

    beefData = this.getValues(false);
        
    Collections.reverse(beefData);
        
    comparedData = CountyData.compareValues(beefData, milkData);
    comparedData = CountyData.resizeValues(comparedData);
    
    numDivs = 4;
    divScale = (int)Math.pow(2, numDivs);

    red = color(255,0,0);
    blue = color(0,0,255);

    colorValues = getColorValues(numDivs);
    
    chart = new Chart(this);

    boolean first = true;
    for(Story story : countyStories) {
      story.attachChart(chart, first);
      first = !first;
    }
  }
  
  County(String name, String abrev,Story[] storyList, int x, int y, int textOffsetX, int textOffsetY){
    this(name, abrev, storyList, x, y);
    
    this.textOffsetX = textOffsetX;
    this.textOffsetY = textOffsetY;
  }
  
  County(String name, String abrev, Story story, int x, int y){
    this(name, abrev, new Story[]{story}, x, y);
  }
  
  County(String name, String abrev, Story story, int x, int y, int textOffsetX, int textOffsetY) {
    this(name, abrev, new Story[]{story}, x, y);
    
    this.textOffsetX = textOffsetX;
    this.textOffsetY = textOffsetY;
  }
  
  County(String name, String abrev, int x, int y){
    this(name, abrev, new Story[]{}, x, y);
  }
  
  County(String name, String abrev, int x, int y, int textOffsetX, int textOffsetY) {
    this(name, abrev, new Story[]{}, x, y, textOffsetX, textOffsetY);
  }
  
  
  ArrayList<Float> getValues(boolean getYearData) {
    String suffix = getYearData == true ? "-milk-data.txt" : "-beef-data.txt";
    
    File f = new File("countyData/"+name+suffix);
    ArrayList<CountyData> yearList = new ArrayList<CountyData>();
    JSONObject json;

    if (f.exists()) {
      String[] lines = loadStrings(f);
      yearList = CountyData.convertArray(lines);

    } 
    else {
      GetRequest get = new GetRequest(this.getAPIString());
      get.send();
      
      String content = get.getContent();
      json = parseJSONObject(content);
  
      JSONArray jsonList = json.getJSONArray("data");
      yearList = CountyData.convertJSON(jsonList); 
    }

    Map<Integer, Float> dataTable = new LinkedHashMap<Integer, Float>();
      
    for (int i = 0; i < yearList.size(); i++) {
      int year = yearList.get(i).year;

      String rawValue = yearList.get(i).value;
      Float value;

      try {
        value = Float.valueOf(rawValue.replace(",", ""));
      }
      catch(NumberFormatException e) {
        value = -1f;
      }

      //Fill in any empty years at the beginning of the list
      if (dataTable.size() == 0) {
        int curYear = 2019;
        while (curYear > year) {
          dataTable.put(curYear, -1f);

          if (getYearData == true)yearData.add(curYear);

          curYear--;
        }
      }
      //Fill in any empty years in the middle of the list
      else {
        int curYear = year + 1;

        while (dataTable.containsKey(curYear) == false) {
          dataTable.put(curYear, -1f);

          if (getYearData == true)yearData.add(curYear);

          curYear += 1;
        }
      }

      dataTable.put(year, value);

      if (getYearData == true && yearData.contains(year) == false)yearData.add(year);
    }

    //Fill in any empty years at the end of the list
    if (dataTable.size() != (2020-firstYear)) {
      int curYear = 2020 - dataTable.size() - 1;
      while (curYear >= firstYear) {
        dataTable.put(curYear, -1f);

        if (getYearData == true)yearData.add(curYear);

        curYear--;
      }
    }

    if(f.exists() == false) writeToFile(dataTable, f);
    
    return new ArrayList<Float>(dataTable.values());
  }
  
  void writeToFile(Map<Integer, Float> data, File f){
    try {
        PrintWriter p = new PrintWriter(f);
        for(Entry<Integer, Float> entry : data.entrySet()){
          p.println(entry.getKey()+":"+entry.getValue());
        }
        p.close();
      }
      catch(FileNotFoundException e) {
      }
  }

  String getAPIString() {
    String url = prefix;
    for (Entry<String, String> entry : params.entrySet()) {
      url += entry.getKey() + "=" + entry.getValue().replaceAll(" ", "%20") + "&";
    }

    return url;
  }  

  ArrayList<Integer> getColorValues(int divCount){
    ArrayList<Integer> colorVals = new ArrayList<Integer>();
    
    for(int i = 0; i < yearData.size(); i++){
      colorVals.add(comparedData.get(i) != -1 ? lerpColor(blue, red, comparedData.get(i)) : color(200));
    } 
        
    if(divCount > 0) colorVals = subDivideColors(colorVals, divCount);
    return colorVals;
  }
  
  ArrayList<Integer> subDivideColors(ArrayList<Integer> vals, int divCount){
    ArrayList<Integer> newVals = new ArrayList<Integer>();

    for(int i = 0; i < vals.size()-1; i++){
      color newVal = lerpColor(vals.get(i), vals.get(i+1), .5);
      ArrayList<Integer> tempVals = new ArrayList<Integer>();
      
      tempVals.add(vals.get(i));
      tempVals.add(newVal);
      tempVals.add(vals.get(i+1));
      
      if(divCount > 1) newVals = addVals(newVals, subDivideColors(tempVals, divCount-1));
      else newVals = addVals(newVals, tempVals);
    }
    
    return newVals;
  }

  ArrayList addVals(ArrayList list1, ArrayList list2){
    if(list1.size() == 0) return list2;
    
    for(int i = 1; i < list2.size(); i++){
      list1.add(list2.get(i));
    }
    
    return list1;
  }

  void render() {  

    textAlign(CENTER);
    
    tint(colorValues.get(iter));
    image(countyImage, x, y);
    noTint();
    
    yearIter = (iter % divScale == 0) ? (int)(iter / divScale) : (int)Math.ceil(iter / divScale); 
    
    textWidth = textWidth(abrev);
    
    textX = x + (countyImage.width / 2) + textOffsetX;
    textY = y + (countyImage.height / 2) + (textSize / 2)  + textOffsetY;
    
    checkMousePos();
    
    text(abrev, textX, textY);
    
    for(Story story : countyStories){
      story.render();
    }
  }
  
  void checkMousePos(){
    if(mouseX >= textX - (textWidth/2)&& mouseX <= textX + (textWidth/2) && mouseY >= textY - textSize && mouseY <= textY){
      fill(252, 226, 5);
      textFont(boldFont);
      hovered = true;
    }
    else{
      if(clicked == false) {
        fill(255);
        textFont(regFont);
      }
      else {
        fill(252, 226, 5);
        textFont(boldFont);
      }
      hovered = false;
    }
  }
  
  boolean mousePressed(){
    if(hovered == true) {
      clicked = !clicked;
      return true;
    }
    return false;
  }
  
  //Returns the story that was pressed, and null if none pressed
  Story storyPressed(){
    for(Story story : countyStories){
      if(story.mousePressed() == true) return story;
    }
    return null;
  }
  
  void stopPressed(){
    clicked = false;
  }
}
