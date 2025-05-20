package app.services;

public class DimensionSvg {
    private double viewboxWidth;
    private double viewboxHeight;
    private Svg dimensionSvg;

    public DimensionSvg(int carportHeight, int carportWidth) {
        this.viewboxWidth = carportWidth + 90;
        this.viewboxHeight = carportHeight + 80;
        dimensionSvg = new Svg(0,0,"0 0 "+viewboxWidth+" "+viewboxHeight,"100%", "100%");

        CarportSvg carportSvg = new CarportSvg(carportHeight,carportWidth);
        dimensionSvg.addSvg(carportSvg.toString());
        addArrows(carportHeight,carportWidth);
    }

    private void addArrows(int carportHeight, int carportWidth){
        dimensionSvg.addArrow(40,10, 40,carportHeight+10);
        dimensionSvg.addText(15, carportHeight/2,"text-anchor: middle",-90, carportHeight+" cm");

        dimensionSvg.addArrow(75,carportHeight + 40, carportWidth+75, carportHeight + 40);
        dimensionSvg.addText((carportWidth/2)+75, carportHeight + 60,"text-anchor: middle",0, carportWidth+" cm");
    }

    @Override
    public String toString() {
        return dimensionSvg.toString();
    }
}
