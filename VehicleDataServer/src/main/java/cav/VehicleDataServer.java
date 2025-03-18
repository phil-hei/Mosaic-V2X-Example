package cav;

import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CamBuilder;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CellModuleConfiguration;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedAcknowledgement;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedV2xMessage;
import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.app.api.CommunicationApplication;
import org.eclipse.mosaic.fed.application.app.api.os.ServerOperatingSystem;
import org.eclipse.mosaic.interactions.communication.V2xMessageTransmission;
import org.eclipse.mosaic.lib.objects.v2x.GenericV2xMessage;
import org.eclipse.mosaic.lib.objects.v2x.V2xMessage;
import org.eclipse.mosaic.lib.util.scheduling.Event;
import org.eclipse.mosaic.rti.DATA;

public class VehicleDataServer extends AbstractApplication<ServerOperatingSystem> implements CommunicationApplication {

    @Override
    public void onStartup() {
        getLog().infoSimTime(this, "Initialize VehicleDataServer application with ID {}", getOs().getId());
        getLog().infoSimTime(this, "Setup VehicleDataServer {} at time {}", getOs().getId(), getOs().getSimulationTime());


        /* Leads to exception
        getOs().getCellModule().enable(
                new CellModuleConfiguration()
                        .maxDownlinkBitrate(5 * DATA.BYTE)
                        .maxUplinkBitrate(5 * DATA.BYTE)
        );
        getLog().infoSimTime(this, "Activated Cell Module");
        */
    }

    @Override
    public void onShutdown() {
        getLog().infoSimTime(this, "Shutdown server.");
    }

    @Override
    public void processEvent(Event event) throws Exception {
    }

    @Override
    public ServerOperatingSystem getOs() {
        return super.getOs();
    }

    @Override
    public void onMessageReceived(ReceivedV2xMessage receivedV2xMessage) {
        final V2xMessage msg = receivedV2xMessage.getMessage();
        getLog().infoSimTime(this, "Received {} from {}.",
                msg.getSimpleClassName(),
                msg.getRouting().getSource().getSourceName()
        );

        if (msg instanceof GenericV2xMessage genericV2xMessage) {
            //VehicleData vehicleData = parseReceivedMessage(genericV2xMessage);
        }
    }

    @Override
    public void onAcknowledgementReceived(ReceivedAcknowledgement receivedAcknowledgement) {
        getLog().infoSimTime(this, "Received Acknowledgement {}", receivedAcknowledgement);
    }

    @Override
    public void onCamBuilding(CamBuilder camBuilder) {

    }

    @Override
    public void onMessageTransmitted(V2xMessageTransmission v2xMessageTransmission) {
        getLog().infoSimTime(this, "Transmitted V2x message {}", v2xMessageTransmission);
    }
}