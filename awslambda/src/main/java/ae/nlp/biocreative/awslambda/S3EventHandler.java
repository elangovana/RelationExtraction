package ae.nlp.biocreative.awslambda;
import ae.nlp.biocreative.Pipeline;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Aws handler that s3 object events to kickoff the relation extraction pipeline
 *
 */
public class S3EventHandler implements RequestHandler<S3Event, String>
{


    private LambdaLogger logger;



    /**
     * Handles the s3 event. Triggers the relation extraction task when a s3 object xml file is created in s3.
     * @param input
     * @param context
     * @return
     */
    public String handleRequest(S3Event input, Context context)
    {
        try
        {
            // Logger
            logger = context.getLogger();

            // Get Event Record
            S3EventNotificationRecord record = input.getRecords().get(0);

            // Source File Name & bucket
            String objectKey = record.getS3().getObject().getKey();
            String bucketName = record.getS3().getBucket().getName();
            logger.log(String.format("Starting trigger for input object %s from bucket %s",  objectKey, bucketName) );

            // Temp local copy of s3 Object
            String tmpInFileName = File.createTempFile("s3Input", ".xml").getAbsolutePath();
            String tmpOutFileName = File.createTempFile("s3Output", ".xml").getAbsolutePath();

            // get reference to S3 client
            AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

            //Write s3 to file
            FileOutputStream fos = null;
            try{
                fos = new FileOutputStream(new File(tmpInFileName));
                CopyS3ObjectToFile(s3, bucketName, objectKey, fos);
            }
            finally {
                if (fos != null)fos.close();

            }

            //Invoke Relation Extraction
            logger.log(String.format("Running the relation extraction pipeline..") );
            Pipeline pipeline = new Pipeline();
            pipeline.runRelationExtraction(tmpInFileName,null, tmpOutFileName);
            logger.log(String.format("Relation extraction pipeline completed") );

            //write output to s3 object
            String outputs3ObjectKey = Paths.get(objectKey, (new File(tmpOutFileName)).getName()).toString();
            logger.log(String.format("Writing the output %s to s3 into bucket %s", outputs3ObjectKey, bucketName) );
            s3.putObject(bucketName, outputs3ObjectKey, new File(tmpOutFileName));

            //LOG
            logger.log("Run completed " );
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return "OK";
    }


    /**
     * Copies an s3 object to an output stream
     * @param s3 AmazonS3
     * @param bucketName S3 Bucket Name
     * @param objectKey S3 Object Key
     * @param outStream Output stream to write the result to
     * @throws IOException
     */
    private void CopyS3ObjectToFile(AmazonS3 s3, String bucketName, String objectKey, OutputStream outStream) throws IOException {
        //Get s3 Object
        S3ObjectInputStream s3InStream = null;
        try{
            logger.log(String.format("Retrieving s3 Object %s from bucket %s",objectKey, bucketName));
            S3Object s3Object = s3.getObject(bucketName, objectKey);
            s3InStream = s3Object.getObjectContent();

            //write stream to file
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3InStream.read(read_buf)) > 0) {
                outStream.write(read_buf, 0, read_len);
            }
        }
        finally {
            if (s3InStream != null)
                s3InStream.close();

        }
    }
}