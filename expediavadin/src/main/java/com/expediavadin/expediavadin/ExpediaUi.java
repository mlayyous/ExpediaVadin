package com.expediavadin.expediavadin;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.expedia.offergenerator.OfferGenerator;
import com.expedia.offergenerator.OfferGeneratorImpl;
import com.expedia.to.*;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class ExpediaUi extends UI {

	private OfferGenerator offerGen = new OfferGeneratorImpl();
	private HotelFilterTo hf;
    private List<Offer> offers;
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final HorizontalLayout fields = new HorizontalLayout();
        final TextField name = new TextField();
        name.setCaption("Destination:");
        final TextField lengthOfstay = new TextField();
        lengthOfstay.setCaption("Length of Stay:");
        DateField minTripStartDate = new DateField();
        minTripStartDate.setCaption("Min trip Date:");
        DateField maxTripStartDate = new DateField();
        maxTripStartDate.setCaption("Max trip Date:");

        fields.addComponents(name,lengthOfstay,minTripStartDate,maxTripStartDate);
        
        final VerticalLayout girdlay = new VerticalLayout();
        Grid<Offer> grid = new Grid<>();
        grid.setSizeFull();
        Button button = new Button("Generate");
        button.addClickListener( e -> {
        	   generateHotelFilter(name.getValue(),lengthOfstay.getValue(),minTripStartDate.getValue(),maxTripStartDate.getValue());
        	   try {
				offers= offerGen.getOffers(hf);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        	   grid.setItems(offers);
               grid.addColumn(Offer::getDestination).setCaption("Destination");
               grid.addColumn(Offer::getLengthOfStay).setCaption("length Of Stay");
               grid.addColumn(Offer::getMinTripStartDate).setCaption("Min trip Date");
               grid.addColumn(Offer::getMaxTripStartDate).setCaption("Max trip Date");
        });
        
        
        girdlay.addComponents(button,grid);
        final VerticalLayout layout = new VerticalLayout();
        layout.addComponents(fields,girdlay);
        setContent(layout);
    }
    
    private void generateHotelFilter(String name, String lengthOfStay ,LocalDate minTripStartDate, LocalDate maxTripStartDate) {
		hf = new HotelFilterTo();
		  HashMap<String, String> map = new HashMap<String, String>();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(":yyyy-MM-dd");
		 if  (name!=null && !name.isEmpty())
		  map.put("destinationName", name);
		 if  (lengthOfStay!=null && !lengthOfStay.isEmpty())
			  map.put("lengthOfStay", lengthOfStay);
		 if  (minTripStartDate!=null){
			  map.put("minTripStartDate", minTripStartDate.format(formatter));
		 }if  (maxTripStartDate!=null){
			  map.put("maxTripStartDate", maxTripStartDate.format(formatter));
		 }
		 hf.setParameter(map);
		
	}

    @WebServlet(urlPatterns = "/*", name = "ExpediaUiServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ExpediaUi.class, productionMode = true)
    public static class ExpediaUiServlet extends VaadinServlet {
    }
}
