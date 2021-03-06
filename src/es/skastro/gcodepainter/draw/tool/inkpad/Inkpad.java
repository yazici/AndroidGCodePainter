package es.skastro.gcodepainter.draw.tool.inkpad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.graphics.PointF;
import android.graphics.RectF;
import es.skastro.gcodepainter.draw.document.Document;
import es.skastro.gcodepainter.draw.document.PointFUtils;
import es.skastro.gcodepainter.draw.document.TracePoint;

public class Inkpad {

    @JsonProperty("points")
    List<TracePoint> points;

    private static ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
    }

    public static Inkpad fromDrawFile(Document file) {
        List<TracePoint> points = file.getPoints();
        if (points.size() > 0)
            points.remove(0);
        return new Inkpad(points);
    }

    public static Inkpad fromFile(File file) {
        try {
            return mapper.readValue(file, Inkpad.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Inkpad() {
    }

    public Inkpad(List<TracePoint> points) {
        this.points = new ArrayList<TracePoint>();
        if (points.size() > 0) {
            this.points.add(new TracePoint(new PointF(0f, 0f)));
            PointF origin = points.get(0).getPoint();
            for (int i = 1; i < points.size(); i++) {
                this.points.add(new TracePoint(PointFUtils.minus(points.get(i).getPoint(), origin)));
            }
        }
    }

    public void saveToDisk(File file) throws IOException {
        File tmp = File.createTempFile("tampon", ".ske");
        OutputStream output = new FileOutputStream(tmp);

        mapper.writeValue(output, this);
        output.close();

        if (file.exists())
            file.delete();
        FileUtils.copyFile(tmp, file);
    }

    public List<TracePoint> getPoints(PointF basePoint, double scale, double angle) {
        List<TracePoint> res = new ArrayList<TracePoint>(points.size());
        for (TracePoint p : points) {
            PointF rotated = PointFUtils.rotate(p.getPoint(), angle);
            res.add(new TracePoint(new PointF((float) (rotated.x * scale + basePoint.x),
                    (float) (rotated.y * scale + basePoint.y))));
        }
        return res;
    }

    @JsonIgnore
    public RectF getBounds() {
        if (bounds == null) {
            PointF p = points.get(0).getPoint();
            if (points.size() == 0)
                bounds = new RectF(0f, 0f, 0f, 0f);
            else
                bounds = new RectF(p.x, p.y, p.x, p.y);
            for (TracePoint tp : points) {
                p = tp.getPoint();
                if (p.x < bounds.left)
                    bounds.left = p.x;
                if (p.x > bounds.right)
                    bounds.right = p.x;
                if (p.y < bounds.bottom)
                    bounds.bottom = p.y;
                if (p.y > bounds.top)
                    bounds.top = p.y;
            }
        }
        return bounds;
    }

    @JsonIgnore
    public PointF getStartOffset() {
        RectF bs = getBounds();
        return new PointF(bs.left, bs.bottom);
    }

    @JsonIgnore
    public PointF getLastOffset() {
        RectF bs = getBounds();
        return new PointF(bs.right, bs.bottom);
    }

    private RectF bounds = null;
}
