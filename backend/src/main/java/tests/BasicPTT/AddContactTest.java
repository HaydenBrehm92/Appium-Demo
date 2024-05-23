package tests.BasicPTT;

import UI.Controllers.MainController;
import core.MyLogger;
import org.testng.Assert;
import org.testng.annotations.Test;
import page_libraries.*;
import tests.BaseTest;
import tests.Data.DataProviders;



public class AddContactTest extends BaseTest {
    @Test (dataProviderClass = DataProviders.class, dataProvider = "AddContactData")
    public void AddContacts(int maxContacts, int delay, Long phoneNum, MainController mainController) throws InterruptedException {
        int total = 0;

        if (MainController.testManager.isRadio()){
            getUIObject(CallScreen.ContactsList).tap();
        }

        mainController.progressBarSetUp();
        mainController.setProgressPerTask(maxContacts, 3);
        for (int i = 0; i < maxContacts; i++){
            try{
                getUIObject(Contact.AddContact).waitUntilClickable(5000).tap();
                getUIObject(Contact.NewContact).waitUntilClickable(5000).tap();
                //Thread.sleep(1500);
                getUIObject(Contact.EnterContactName).inputKeys("Contact #" + (i + 1));
                mainController.taskComplete();
                //Thread.sleep(1500);
                getUIObject(Contact.EnterPhoneNumber).inputKeys(Long.toString(phoneNum));
                mainController.taskComplete();
                getUIObject(Contact.SaveContact).waitUntilClickable(5000).tap();
                mainController.taskComplete();
                mainController.log("Completed Add Contact #" + (i + 1));
                phoneNum ++;
                total++;
                //Thread.sleep(1500);
                if(i != (maxContacts - 1)) {Thread.sleep(delay);}
            }catch(Exception e)
            {
                if(getUIObject(Contact.ContactMaxInformationPopup).elementExists())
                {
                    MyLogger.log.info("Max Number of contacts added");
                    //getUIObject(Contact.ContactMaxInformationPopupConfirmation).tap();
                    getUIObject(Common.OK).tap();
                }
                MyLogger.log.debug("Encountered Error: {}", e.getMessage());
                Assert.assertEquals(total, maxContacts, "Mismatch # of Contacts added");
                Thread.sleep(10000);
            }
        }
        mainController.completeProgressBar(); //outside loop, just complete 100% before test ends
        Assert.assertEquals(total, maxContacts, "Mismatch # of Contacts added");
        Thread.sleep(10000);
    }


    @Override
    public void resetToDefaultLocation() {

    }

    @Override
    public void recover() {

    }

    @Override
    public void testLoop() {

    }
}
