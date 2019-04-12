package tt_myTomcat;

public class CloudServlet implements HttpServlet {

    @Override
    public String doGet() {
        return this.doPost();
    }

    @Override
    public String doPost() {
        return "<h1>Hellow Word!!!</h1>";
    }

}