package app.services;

public class CarportSvg {
    private int width;
    private int height;
    private Svg carportSvg;
    private String rectStyle = "stroke:black;fill: white";
    private Calculator calculator;
    private static final int UPPERBEAM_Y = 35;  //stolperne/remme sættes altid 35 cm inde
    private static final int LOWERBEAM_Y = -35; //For spejlvendt at ramme samme længde ind til remmen, så vil det blive 40cm (da vi tæller stolpens/remmens bredde med)

    public CarportSvg(int height, int width) {
        this.width = width;
        this.height = height;
        calculator = new Calculator(height,width);
        carportSvg = new Svg(75,10,"0 0 "+width+" "+height,width+"", height+"");
        carportSvg.addRectangle(0,0,this.height, this.width, rectStyle);

        addBeams();
        addRafters();
        addPosts();
        addShed();
    }

    private void addBeams (){
        carportSvg.addRectangle(0,UPPERBEAM_Y,5,this.width, rectStyle);
        carportSvg.addRectangle(0, this.height+LOWERBEAM_Y,5,this.width, rectStyle);
    }

    private void addRafters(){
        int calculatedRafters = calculator.calcRaftersQuantity();

        for(int i = 0; i<=this.width; i += (width/calculatedRafters)){
        carportSvg.addRectangle(i,0,this.height,5,rectStyle);}

    }

    private void addPosts(){
        int firstPostX = 100; //En meter inde i carporten
        int lastPostX = this.width-30; //30 cm inde for carportens bagende
        int postWidthAndHeight = 10; //Både bredde og højde udgør 10 cm

        //Upper posts
        carportSvg.addRectangle(firstPostX,UPPERBEAM_Y,postWidthAndHeight,postWidthAndHeight, rectStyle);
        carportSvg.addRectangle(lastPostX,UPPERBEAM_Y,postWidthAndHeight,postWidthAndHeight, rectStyle);

        //lower posts
        carportSvg.addRectangle(firstPostX,this.height+LOWERBEAM_Y,postWidthAndHeight,postWidthAndHeight, rectStyle);
        carportSvg.addRectangle(lastPostX,this.height+LOWERBEAM_Y,postWidthAndHeight,postWidthAndHeight, rectStyle);

        if(calculator.calcPostQuantity() > 4) {
            int centerPostX = firstPostX + ((lastPostX-firstPostX)/2); // = mellemrummet mellem forreste og bagerste stolpe, divideret i to for at sætte mellemste stolpe i midten af de to andre.

            carportSvg.addRectangle(centerPostX, UPPERBEAM_Y, postWidthAndHeight, postWidthAndHeight, rectStyle);
            carportSvg.addRectangle(centerPostX, this.height+LOWERBEAM_Y, postWidthAndHeight, postWidthAndHeight, rectStyle);
        }
    }

    private void addShed(){
        String spaceStyle = "stroke:black;stroke-dasharray:10,5";
        carportSvg.addLine(55, 40, width*0.7, height-35, spaceStyle);
        carportSvg.addLine(55, height-35,width*0.7, 40, spaceStyle);
    }




    @Override
    public String toString() {
        return carportSvg.toString();
    }
}
