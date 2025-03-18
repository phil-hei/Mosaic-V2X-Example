package cav;

import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CamBuilder;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.CellModuleConfiguration;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedAcknowledgement;
import org.eclipse.mosaic.fed.application.ambassador.simulation.communication.ReceivedV2xMessage;
import org.eclipse.mosaic.fed.application.app.AbstractApplication;
import org.eclipse.mosaic.fed.application.app.api.CommunicationApplication;
import org.eclipse.mosaic.fed.application.app.api.VehicleApplication;
import org.eclipse.mosaic.fed.application.app.api.os.VehicleOperatingSystem;
import org.eclipse.mosaic.interactions.communication.V2xMessageTransmission;
import org.eclipse.mosaic.lib.objects.ToDataOutput;
import org.eclipse.mosaic.lib.objects.v2x.GenericV2xMessage;
import org.eclipse.mosaic.lib.objects.v2x.MessageRouting;
import org.eclipse.mosaic.lib.objects.vehicle.VehicleData;
import org.eclipse.mosaic.lib.util.scheduling.Event;
import org.eclipse.mosaic.rti.DATA;
import org.eclipse.mosaic.rti.TIME;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class VehicleDataCollector extends AbstractApplication<VehicleOperatingSystem> implements VehicleApplication, CommunicationApplication {
    private MessageRouting mr;

    @Override
    public void onStartup() {
        getLog().infoSimTime(this, "Initialize application for vehicle {} of type {}", getOs().getId(), getOs().getVehicleParameters().getInitialVehicleType().getVehicleClass());

        getOs().getCellModule().enable(new CellModuleConfiguration()
                .maxDownlinkBitrate(50 * DATA.MEGABIT)
                .maxUplinkBitrate(50 * DATA.MEGABIT)
        );
        getLog().infoSimTime(this, "Activated Cell Module");

        // A MessageRouting object contains a source and a target address for a message to be routed.
        mr = getOs().getCellModule().createMessageRouting().topoCast("server_0");
    }

    @Override
    public void onShutdown() {
        getLog().infoSimTime(this, "Shutdown");
    }

    @Override
    public void processEvent(Event event) throws Exception {
    }

    @Override
    public void onVehicleUpdated(@Nullable VehicleData vehicleData, @Nonnull VehicleData vehicleData1) {
        if (vehicleData == null) {
            return;
        }

        double speed = vehicleData.getSpeed();

        ToDataOutput msgPayLoad = null;
        try {
            msgPayLoad = convertToDataOutput(speed);
            GenericV2xMessage message = new GenericV2xMessage(mr, msgPayLoad, 0);
            getOs().getCellModule().sendV2xMessage(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private ToDataOutput convertToDataOutput(double speed) throws IOException {
        // Serialize VehicleData to a byte array
        byte[] serializedData = serializeToBytes(speed);

        // Wrap it inside a ToDataOutput object
        return new ToDataOutput() {
            @Override
            public void toDataOutput(java.io.DataOutput out) throws IOException {
                out.write(serializedData);
            }
        };
    }

    private byte[] serializeToBytes(double speed) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

             objectOutputStream.writeObject(speed); // Serialize the object
             objectOutputStream.flush();

             return byteArrayOutputStream.toByteArray(); // Return the serialized data
        }
    }

    @Override
    public void onMessageReceived(ReceivedV2xMessage receivedV2xMessage) {

    }

    @Override
    public void onAcknowledgementReceived(ReceivedAcknowledgement receivedAcknowledgement) {

    }

    @Override
    public void onCamBuilding(CamBuilder camBuilder) {

    }

    @Override
    public void onMessageTransmitted(V2xMessageTransmission v2xMessageTransmission) {
        getLog().info("Message transmitted");
    }
}