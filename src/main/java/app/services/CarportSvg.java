package app.services;

public class CarportSvg {
    private int width;
    private int height;
    private Svg carportSvg;
    private String rectStyle = "stroke:black;fill: white";
    private Calculator calculator;

    public CarportSvg(int height, int width) {
        this.width = width;
        this.height = height;
        calculator = new Calculator(height,width);
        carportSvg = new Svg(0,0,"100%", "0 0 855 690");
        carportSvg.addRectangle(0,0,this.height, this.width, rectStyle);

        addBeams();
        addRafters();
        addPosts();
    }

    private void addBeams (){
        carportSvg.addRectangle(0,0+35,5,this.width, rectStyle);
        carportSvg.addRectangle(0, this.height-40,5,this.width, rectStyle);
    }

    private void addRafters(){
        int calculatedRafters = calculator.calcRaftersQuantity();

        for(int i = 0; i<=this.width; i += width/calculatedRafters){
        carportSvg.addRectangle(i,0,this.height,5,rectStyle);}

    }

    private void addPosts(){
        int firstPostX = 100; //En meter inde i carporten
        int lastPostX = this.width-30; //30 cm inde for carportens bagende
        int postWidthAndHeight = 10; //Både bredde og højde udgør 10 cm

        int upperY = 35; //stolperne sættes altid 35 cm inde
        int lowerY = height-40; //For spejlvendt at ramme samme længde ind til remmen, så vil det blive 40cm (da vi tæller stolpens bredde med)

        //Upper posts
        carportSvg.addRectangle(firstPostX,upperY,postWidthAndHeight,postWidthAndHeight, rectStyle);
        carportSvg.addRectangle(lastPostX,upperY,postWidthAndHeight,postWidthAndHeight, rectStyle);

        //lower posts
        carportSvg.addRectangle(firstPostX,lowerY,postWidthAndHeight,postWidthAndHeight, rectStyle);
        carportSvg.addRectangle(lastPostX,lowerY,postWidthAndHeight,postWidthAndHeight, rectStyle);

        if(calculator.calcPostQuantity() > 4) {
            int centerPostX = firstPostX + ((lastPostX-firstPostX)/2); // = mellemrummet mellem forreste og bagerste stolpe, divideret i to for at sætte mellemste stolpe i midten af de to andre.

            carportSvg.addRectangle(centerPostX, upperY, postWidthAndHeight, postWidthAndHeight, rectStyle);
            carportSvg.addRectangle(centerPostX, lowerY, postWidthAndHeight, postWidthAndHeight, rectStyle);
        }
    }

    @Override
    public String toString() {
        return carportSvg.toString();
    }
}
