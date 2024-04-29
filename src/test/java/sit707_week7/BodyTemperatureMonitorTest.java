package sit707_week7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import sit707_week7.sample.EmailSender;
import sit707_week7.sample.Student;
import sit707_week7.sample.StudentReader;
import sit707_week7.sample.StudentRepository;

public class BodyTemperatureMonitorTest {

	private BodyTemperatureMonitor tempMonitor;
	private TemperatureSensor temperatureSensor;
	private CloudService cloudService;
	private Customer customer;
	private NotificationSender notificationSender;
	private FamilyDoctor familyDoctor;

	
	private TemperatureReading tempReading;
	
	@Before
	public void setUp() {
		temperatureSensor = Mockito.mock(TemperatureSensor.class);
		cloudService = Mockito.mock(CloudService.class);
		notificationSender = Mockito.mock(NotificationSender.class);
		tempReading = new TemperatureReading();
		customer = new Customer();
		familyDoctor = new FamilyDoctor();
		
		tempMonitor = new BodyTemperatureMonitor(temperatureSensor, cloudService, notificationSender);
	}
	
	@Test
	public void testStudentIdentity() {
		String studentId = "224114235";
		Assert.assertNotNull("Student ID is null", studentId);
	}

	@Test
	public void testStudentName() {
		String studentName = "Shreya";
		Assert.assertNotNull("Student name is null", studentName);
	}
	

	
	
	@Test
	public void testReadTemperatureNegative() {
		Mockito.when(temperatureSensor.readTemperatureValue()).thenReturn(-2.0);
		double temp = tempMonitor.readTemperature();
		Assert.assertEquals(temp, -2.0, 0);
	}
	
	@Test
	public void testReadTemperatureZero() {
		Mockito.when(temperatureSensor.readTemperatureValue()).thenReturn(0.0);
		double temp = tempMonitor.readTemperature();
		Assert.assertEquals(temp, 0.0, 0);
	}
	
	@Test
	public void testReadTemperatureNormal() {
		Mockito.when(temperatureSensor.readTemperatureValue()).thenReturn(10.0);
		double temp = tempMonitor.readTemperature();
		Assert.assertEquals(temp, 10.0, 0);
	}

	@Test
	public void testReadTemperatureAbnormallyHigh() {
		Mockito.when(temperatureSensor.readTemperatureValue()).thenReturn(2000.0);
		double temp = tempMonitor.readTemperature();
		Assert.assertEquals(temp, 2000.0, 0);
	}

	

	
	@Test
	public void testReportTemperatureReadingToCloud() {
		// Mock reportTemperatureReadingToCloud() calls cloudService.sendTemperatureToCloud()
		tempMonitor.reportTemperatureReadingToCloud(tempReading);
		Mockito.verify(cloudService).sendTemperatureToCloud(tempReading);
	}
	
	
	@Test
	public void testInquireBodyStatusNormalNotification() {
		
		Mockito.when(cloudService.queryCustomerBodyStatus(any())).thenReturn("NORMAL");
        // Execute
		tempMonitor.inquireBodyStatus();
        // Verify
		Mockito.verify(notificationSender).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));
		Mockito.verify(notificationSender, never()).sendEmailNotification(any(Customer.class), eq("Emergency!"));
    
	}
	
	
	@Test
	public void testInquireBodyStatusAbnormalNotification() {
		Mockito.when(cloudService.queryCustomerBodyStatus(any())).thenReturn("ABNORMAL");
        // Execute
		tempMonitor.inquireBodyStatus();
        // Verify
		Mockito.verify(notificationSender).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
		Mockito.verify(notificationSender, never()).sendEmailNotification(any(FamilyDoctor.class), eq("Thumbs Up!"));
    
	}
	
	@Test
	public void testGetFixedcustomer() {
		Customer expectedCustomer = new Customer();

        // Execute
        Customer actualCustomer = tempMonitor.getFixedCustomer();

        // Verify
        assertNotNull(actualCustomer);

	}
	
	@Test
	public void testGetFamilyDoctor() {
        // Execute
        FamilyDoctor familyDoctor = tempMonitor.getFamilyDoctor();

        // Verify
        assertNotNull(familyDoctor);

	}
}
