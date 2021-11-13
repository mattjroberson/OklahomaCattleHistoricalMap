ArrayList<County> getCounties(){
  //Current maximum stories per county: 2. Tabs need work for more
  
  ArrayList<County> countyList = new ArrayList<County>();
  
  Story comancheStory = new Story("comanche1", "https://dc.library.okstate.edu/digital/collection/Farm/id/3895/rec/1");
  storyList.add(comancheStory);
  
  Story kingfisherStory = new Story("kingfisher1", "https://dc.library.okstate.edu/digital/collection/Farm/id/3951/rec/1");
  Story kingfisherStory2 = new Story("kingfisher2", "https://oklahoman.com/article/3100146/got-cowsbrspan-classhl2small-dairies-sell-out-as-costs-competition-risespan");
  storyList.add(kingfisherStory);
  storyList.add(kingfisherStory2);
  
  Story payneStory = new Story("payne1", "https://dc.library.okstate.edu/digital/collection/Farm/id/3571/rec/1");
  storyList.add(payneStory);
  
  Story lincolnStory = new Story("lincoln1",  "https://dc.library.okstate.edu/digital/collection/Farm/id/3626/rec/11");
  Story lincolnStory2 = new Story("lincoln2", "https://dc.library.okstate.edu/digital/collection/Farm/id/3714/rec/1");
  storyList.add(lincolnStory);
  storyList.add(lincolnStory2);
  
  Story nobleStory = new Story("noble1", "https://dc.library.okstate.edu/digital/collection/Farm/id/3796/rec/4");
  storyList.add(nobleStory);
  
  Story murrayStory = new Story("murray1", "https://oklahoman.com/article/3100146/got-cowsbrspan-classhl2small-dairies-sell-out-as-costs-competition-risespan");
  storyList.add(murrayStory);
  
  countyList.add(new County("ADAIR", "AD", 1233, 305));
  countyList.add(new County("ALFALFA", "AL", 711, 164));
  countyList.add(new County("ATOKA", "AT", 1012, 562));
  countyList.add(new County("BEAVER", "BV", 367, 164));
  countyList.add(new County("BECKHAM", "BK", 505, 416));
  countyList.add(new County("BLAINE", "BL", 699, 308));
  countyList.add(new County("BRYAN", "BR", 984, 652));
  countyList.add(new County("CADDO", "CA", 699, 415));
  countyList.add(new County("CANADIAN", "CN", 739, 386));
  countyList.add(new County("CARTER", "CR", 846, 595));
  countyList.add(new County("CHEROKEE", "CZ", 1165, 305, 5, 0));
  countyList.add(new County("CHOCTAW", "CW", 1077, 652));
  countyList.add(new County("CIMARRON", "CI", 93, 164));
  countyList.add(new County("CLEVELAND", "CL", 831, 446, 15, -10));
  countyList.add(new County("COAL", "CO", 996, 550));
  countyList.add(new County("COMANCHE", "CC", comancheStory, 666, 533));
  countyList.add(new County("COTTON", "CT", 688, 593));
  countyList.add(new County("CREEK", "CG", 978, 308));
  countyList.add(new County("CRAIG", "CK", 1144, 164));
  countyList.add(new County("CUSTER", "CS", 592, 370));
  countyList.add(new County("DELAWARE", "DL", 1201, 218));
  countyList.add(new County("DEWEY", "DW", 592, 307));
  countyList.add(new County("ELLIS", "EL", 505, 235, -10, 0));
  countyList.add(new County("GARFIELD", "GA", 768, 233));
  countyList.add(new County("GARVIN", "GV", 831, 533));
  countyList.add(new County("GRADY", "GD", 771, 448));
  countyList.add(new County("GRANT", "GT", 768, 164));
  countyList.add(new County("GREER", "GE", 514, 488));
  countyList.add(new County("HARMON", "HM", 505, 503, -3, 0));
  countyList.add(new County("HARPER", "HP", 505, 164));
  countyList.add(new County("HASKELL", "HK", 1146, 427, 0, 10));
  countyList.add(new County("HUGHES", "HG", 999, 460));
  countyList.add(new County("JACKSON", "JA", 523, 535));
  countyList.add(new County("JEFFERSON", "JE", 765, 634, 0, -15));
  countyList.add(new County("JOHNSTON", "JN", 934, 595));
  countyList.add(new County("KAY", "KA", 862, 164));
  countyList.add(new County("KINGFISHER", "KF", new Story[]{kingfisherStory, kingfisherStory2}, 756, 308));
  countyList.add(new County("KIOWA", "KW", 594, 491));
  countyList.add(new County("LATIMER", "LA", 1140, 496));
  countyList.add(new County("LEFLORE", "LE", 1203, 440));
  countyList.add(new County("LINCOLN", "LN", new Story[]{lincolnStory, lincolnStory2}, 907, 350));
  countyList.add(new County("LOGAN", "LG", 831, 308));
  countyList.add(new County("LOVE", "LV", 846, 670, 0, -15));
  countyList.add(new County("MAJOR", "MA", 651, 250));
  countyList.add(new County("MARSHALL", "MR", 930, 652));
  countyList.add(new County("MAYES", "ME", 1144, 247));
  countyList.add(new County("MCINTOSH", "MO", 1069, 412, 0, -5));
  countyList.add(new County("MCCLAIN", "ML", 831, 457, -25, 20));
  countyList.add(new County("MCCURTAIN", "MC", 1192, 590));
  countyList.add(new County("MURRAY", "MU", murrayStory, 877, 571, 10, 0));
  countyList.add(new County("MUSKOGEE", "MK", 1098, 362, 0, -15));
  countyList.add(new County("NOBLE", "NB", nobleStory, 862, 233));
  countyList.add(new County("NOWATA", "NW", 1098, 164));
  countyList.add(new County("OKFUSKEE", "OF", 979, 398));
  countyList.add(new County("OKLAHOMA", "OK", 831, 386));
  countyList.add(new County("OKMULGEE", "OM", 1039, 362, 0, -5));
  countyList.add(new County("OSAGE", "OS", 913, 164, 25, 0));
  countyList.add(new County("OTTAWA", "OT", 1201, 164));
  countyList.add(new County("PONTOTOC", "PN", 936, 515));
  countyList.add(new County("POTTAWATOMIE", "PT", 907, 431, -10, 0));
  countyList.add(new County("PAWNEE", "PW", 919, 241, -10, 10));
  countyList.add(new County("PAYNE", "PA", payneStory, 874, 296, 10, 0));
  countyList.add(new County("PITTSBURG", "PB", 1057, 455));
  countyList.add(new County("PUSHMATAHA", "PM", 1102, 562));
  countyList.add(new County("ROGER MILLS", "RM", 505, 331));
  countyList.add(new County("ROGERS", "RG", 1089, 230));
  countyList.add(new County("SEMINOLE", "SM", 954, 431));
  countyList.add(new County("SEQUOYAH", "SY", 1188, 394, 0, -5));
  countyList.add(new County("STEPHENS", "ST", 763, 563));
  countyList.add(new County("TEXAS", "TX", 231, 164));
  countyList.add(new County("TILLMAN", "TL", 606, 569));
  countyList.add(new County("TULSA",  "TU", 1023, 265, 20, 10));
  countyList.add(new County("WAGONER", "WG", 1089, 310));
  countyList.add(new County("WASHINGTON", "WA", 1066, 164));
  countyList.add(new County("WASHITA", "WT", 592, 430));
  countyList.add(new County("WOODS", "WD", 589, 164, 15, -5));
  countyList.add(new County("WOODWARD", "WW", 561, 202));
  
  return countyList;
}
