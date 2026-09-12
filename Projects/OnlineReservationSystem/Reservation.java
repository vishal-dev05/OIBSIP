/**
 * Simple data holder (POJO) for one reservation/booking.
 */
public class Reservation {

    private String pnr;
    private String passengerName;
    private String trainNumber;
    private String trainName;
    private String classType;
    private String journeyDate;   // stored as dd-MM-yyyy text
    private String source;
    private String destination;

    public Reservation() {
    }

    public Reservation(String pnr, String passengerName, String trainNumber, String trainName,
                        String classType, String journeyDate, String source, String destination) {
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.classType = classType;
        this.journeyDate = journeyDate;
        this.source = source;
        this.destination = destination;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getClassType() {
        return classType;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    /** Multi-line, human-readable summary used in confirmation/fetch dialogs. */
    public String toDisplayString() {
        return "PNR: " + pnr
                + "\nPassenger Name: " + passengerName
                + "\nTrain Number: " + trainNumber
                + "\nTrain Name: " + trainName
                + "\nClass: " + classType
                + "\nDate of Journey: " + journeyDate
                + "\nFrom: " + source
                + "\nTo: " + destination;
    }
}
