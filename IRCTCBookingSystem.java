// PASSENGER RESERVATION SYSTEM
// TICKET CLERK
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

public class IRCTCBookingSystem extends JFrame {
    private JTextField nameField, ageField, mobileField;
    private JComboBox<String> trainCombo, classCombo, sourceCombo, destinationCombo;
    private JRadioButton maleRadio, femaleRadio;
    private JButton bookButton, clearButton;
    private JTextArea bookingSummary;
    private JSpinner dateSpinner;

    // Mock database for trains
    private static class Train {
        String name;
        int totalSeats;
        int availableSeats;
        String[] stations;
        Map<String, Integer> distances; // Distance from source station to others in km

        Train(String name, int totalSeats, String[] stations, Map<String, Integer> distances) {
            this.name = name;
            this.totalSeats = totalSeats;
            this.availableSeats = totalSeats;
            this.stations = stations;
            this.distances = distances;
        }
    }

    private final Map<String, Train> trainDatabase = new HashMap<>();

    public IRCTCBookingSystem() {
        // Initialize mock database
        initializeDatabase();

        // Frame setup
        setTitle("IRCTC Railway Booking System");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Passenger Details
        inputPanel.add(new JLabel("Passenger Name:"));
        nameField = new JTextField(20);
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Age:"));
        ageField = new JTextField(20);
        inputPanel.add(ageField);

        inputPanel.add(new JLabel("Mobile Number:"));
        mobileField = new JTextField(20);
        inputPanel.add(mobileField);

        // Gender Selection
        inputPanel.add(new JLabel("Gender:"));
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        inputPanel.add(genderPanel);

        // Train Selection
        inputPanel.add(new JLabel("Select Train:"));
        trainCombo = new JComboBox<>(new String[]{"Rajdhani Express", "Shatabdi Express", "Vande Bharat Express"});
        inputPanel.add(trainCombo);

        // Source Station
        inputPanel.add(new JLabel("Source Station:"));
        sourceCombo = new JComboBox<>(new String[]{"Chandigarh", "Delhi", "Ambala", "Ludhiana", "Jalandhar"});
        inputPanel.add(sourceCombo);

        // Destination Station
        inputPanel.add(new JLabel("Destination Station:"));
        destinationCombo = new JComboBox<>(new String[]{"Chandigarh", "Delhi", "Ambala", "Ludhiana", "Jalandhar"});
        inputPanel.add(destinationCombo);

        // Date of Journey
        inputPanel.add(new JLabel("Date of Journey:"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        inputPanel.add(dateSpinner);

        // Class Selection
        inputPanel.add(new JLabel("Travel Class:"));
        classCombo = new JComboBox<>(new String[]{"AC First Class", "AC 2 Tier", "AC 3 Tier", "Sleeper", "General"});
        inputPanel.add(classCombo);

        // Buttons
        bookButton = new JButton("Book Ticket");
        clearButton = new JButton("Clear Form");
        inputPanel.add(bookButton);
        inputPanel.add(clearButton);

        // Booking Summary Area
        bookingSummary = new JTextArea(12, 30);
        bookingSummary.setEditable(false);
        bookingSummary.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(bookingSummary);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookTicket();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
    }

    private void initializeDatabase() {
        String[] stations = {"Chandigarh", "Delhi", "Ambala", "Ludhiana", "Jalandhar"};
        // Distances from each station to others (in km)
        Map<String, Integer> rajdhaniDistances = new HashMap<>();
        rajdhaniDistances.put("Delhi", 250);
        rajdhaniDistances.put("Ambala", 50);
        rajdhaniDistances.put("Ludhiana", 100);
        rajdhaniDistances.put("Jalandhar", 150);
        rajdhaniDistances.put("Chandigarh", 0); // Base station

        Map<String, Integer> shatabdiDistances = new HashMap<>(rajdhaniDistances);
        Map<String, Integer> vandeBharatDistances = new HashMap<>(rajdhaniDistances);

        trainDatabase.put("Rajdhani Express", new Train("Rajdhani Express", 100, stations, rajdhaniDistances));
        trainDatabase.put("Shatabdi Express", new Train("Shatabdi Express", 50, stations, shatabdiDistances));
        trainDatabase.put("Vande Bharat Express", new Train("Vande Bharat Express", 1, stations, vandeBharatDistances));
    }

    private void bookTicket() {
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String mobile = mobileField.getText().trim();
        String gender = maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : "";
        String trainName = (String) trainCombo.getSelectedItem();
        String source = (String) sourceCombo.getSelectedItem();
        String destination = (String) destinationCombo.getSelectedItem();
        Date journeyDate = (Date) dateSpinner.getValue();
        String travelClass = (String) classCombo.getSelectedItem();

        // Validation
        if (name.isEmpty() || ageText.isEmpty() || mobile.isEmpty() || gender.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Pattern.matches("[a-zA-Z\\s]+", name)) {
            JOptionPane.showMessageDialog(this, "Name should contain only letters and spaces.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            if (age < 1 || age > 120) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age (1-120).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Pattern.matches("\\d{10}", mobile)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 10-digit mobile number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (source.equals(destination)) {
            JOptionPane.showMessageDialog(this, "Source and destination cannot be the same.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date today = new Date();
        if (journeyDate.before(today)) {
            JOptionPane.showMessageDialog(this, "Journey date cannot be in the past.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Train train = trainDatabase.get(trainName);
        if (train.availableSeats <= 0) {
            JOptionPane.showMessageDialog(this, "No seats available for " + trainName + ".", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate distance and fare
        int distance = Math.abs(train.distances.get(destination) - train.distances.get(source));
        double fareMultiplier;
        switch (travelClass) {
            case "AC First Class": fareMultiplier = 5.0; break;
            case "AC 2 Tier": fareMultiplier = 3.5; break;
            case "AC 3 Tier": fareMultiplier = 2.5; break;
            case "Sleeper": fareMultiplier = 1.5; break;
            default: fareMultiplier = 1.0; // General
        }
        double fare = distance * fareMultiplier;

        // Update seat availability
        train.availableSeats--;

        // Generate PNR
        long pnr = (long) (Math.random() * 10000000000L);

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String journeyDateStr = sdf.format(journeyDate);

        // Generate Booking Summary
        String summary = String.format(
                "Booking Confirmation\n" +
                "--------------------\n" +
                "PNR Number: %d\n" +
                "Passenger Name: %s\n" +
                "Age: %s\n" +
                "Gender: %s\n" +
                "Mobile Number: %s\n" +
                "Train: %s\n" +
                "Source: %s\n" +
                "Destination: %s\n" +
                "Date of Journey: %s\n" +
                "Travel Class: %s\n" +
                "Distance: %d km\n" +
                "Fare: ₹%.2f\n" +
                "Seats Available: %d\n" +
                "--------------------\n" +
                "Booking Status: Confirmed\n",
                pnr, name, ageText, gender, mobile, trainName, source, destination, journeyDateStr, travelClass, distance, fare, train.availableSeats
        );

        bookingSummary.setText(summary);

        // Generate Ticket File
        generateTicketFile(pnr, name, ageText, gender, mobile, trainName, source, destination, journeyDateStr, travelClass, distance, fare);

        JOptionPane.showMessageDialog(this, "Ticket booked successfully! Ticket saved as ticket_" + pnr + ".txt", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateTicketFile(long pnr, String name, String age, String gender, String mobile, String trainName,
                                    String source, String destination, String journeyDate, String travelClass, int distance, double fare) {
        String ticketContent = String.format(
                "IRCTC Railway Ticket\n" +
                "--------------------\n" +
                "PNR Number: %d\n" +
                "Passenger Name: %s\n" +
                "Age: %s\n" +
                "Gender: %s\n" +
                "Mobile Number: %s\n" +
                "Train: %s\n" +
                "Source: %s\n" +
                "Destination: %s\n" +
                "Date of Journey: %s\n" +
                "Travel Class: %s\n" +
                "Distance: %d km\n" +
                "Fare: ₹%.2f\n" +
                "--------------------\n" +
                "Status: Confirmed\n" +
                "Thank you for booking with IRCTC!\n",
                pnr, name, age, gender, mobile, trainName, source, destination, journeyDate, travelClass, distance, fare
        );

        try (FileWriter writer = new FileWriter("ticket_" + pnr + ".txt")) {
            writer.write(ticketContent);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving ticket file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        ageField.setText("");
        mobileField.setText("");
        maleRadio.setSelected(false);
        femaleRadio.setSelected(false);
        trainCombo.setSelectedIndex(0);
        sourceCombo.setSelectedIndex(0);
        destinationCombo.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
        classCombo.setSelectedIndex(0);
        bookingSummary.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new IRCTCBookingSystem().setVisible(true);
        });
    }
}