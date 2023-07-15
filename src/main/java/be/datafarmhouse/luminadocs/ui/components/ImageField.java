package be.datafarmhouse.luminadocs.ui.components;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import org.hibernate.engine.jdbc.BlobProxy;

import java.sql.Blob;
import java.sql.SQLException;

public class ImageField extends AbstractCompositeField<VerticalLayout, ImageField, Blob> {

    private Blob value;
    private Image image;
    private Upload upload;
    private MemoryBuffer buffer;

    public ImageField(final Blob defaultValue) {
        super(defaultValue);
        getContent().setWidthFull();
        getContent().setPadding(false);
        upload = new Upload(buffer = new MemoryBuffer());
        upload.setWidthFull();
        image = new Image();

        upload.setAcceptedFileTypes("image/*");
        upload.addSucceededListener(event -> {
            try {
                final Blob blob = BlobProxy.generateProxy(
                        buffer.getInputStream(),
                        event.getContentLength()
                );
                setValue(blob);
                setModelValue(blob, true);
                upload.clearFileList();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });

        getContent().add(upload, image);
    }

    private void updateImage(final Blob blob) {
        if (blob != null) {
            image.setSrc(new StreamResource("image.jpg",
                    () -> {
                        try {
                            return blob.getBinaryStream();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }));
        } else {
            image.setSrc("");
        }
    }

    public void setValue(final Blob value) {
        this.value = value;
        updateImage(value);
    }

    public Blob getValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(final Blob value) {
        setValue(value);
    }
}
