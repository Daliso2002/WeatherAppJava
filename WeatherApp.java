import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class WeatherApp extends JFrame {
    private static final String API_KEY = "ENTER YOUR API KEY";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    
    private JTextField cityField;
    private JButton getWeatherButton;
    private JLabel tempLabel;
    private JLabel weatherLabel;

    public WeatherApp() {
        setTitle("Weather App");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for user input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        
        cityField = new JTextField(15);
        getWeatherButton = new JButton("Get Weather");
        
        inputPanel.add(new JLabel("Enter City:"));
        inputPanel.add(cityField);
        inputPanel.add(getWeatherButton);
        
        // Panel for displaying weather information
        JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new GridLayout(2, 1));
        
        tempLabel = new JLabel("Temperature: ");
        tempLabel.setHorizontalAlignment(SwingConstants.CENTER);
        weatherLabel = new JLabel("Weather: ");
        weatherLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        weatherPanel.add(tempLabel);
        weatherPanel.add(weatherLabel);
        
        add(inputPanel, BorderLayout.NORTH);
        add(weatherPanel, BorderLayout.CENTER);

        getWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = cityField.getText().trim();
                if (!city.isEmpty()) {
                    getWeather(city);
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a city name.");
                }
            }
        });
    }

    private void getWeather(String city) {
        try {
            String urlString = BASE_URL + city + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                // Convert content to a String for manual parsing
                String jsonResponse = content.toString();

                // Find temperature
                String tempMarker = "\"temp\":";
                int tempIndex = jsonResponse.indexOf(tempMarker) + tempMarker.length();
                String tempStr = jsonResponse.substring(tempIndex, jsonResponse.indexOf(",", tempIndex)).trim();
                double temp = Double.parseDouble(tempStr);

                // Find weather description
                String weatherMarker = "\"description\":\"";
                int weatherIndex = jsonResponse.indexOf(weatherMarker) + weatherMarker.length();
                String weather = jsonResponse.substring(weatherIndex, jsonResponse.indexOf("\"", weatherIndex));

                // Update GUI labels
                tempLabel.setText("Temperature: " + temp + "°C");
                weatherLabel.setText("Weather: " + weather);

            } else {
                JOptionPane.showMessageDialog(null, "Error: Unable to fetch data. Please check the city name.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while fetching data.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherApp app = new WeatherApp();
            app.setVisible(true);
        });
    }
}
