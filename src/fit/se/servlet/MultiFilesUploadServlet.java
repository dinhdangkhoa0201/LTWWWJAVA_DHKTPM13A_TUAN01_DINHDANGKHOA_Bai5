package fit.se.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class MultiFilesUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_DIRECTORY = "upload";
	private static final int THRESHOLD_SIZE = 1024*1024*3;
	private static final int MAX_FILE_SIZE = 1024*1024*10;
	private static final int REQUEST_SIZE = 1024*1024*50;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(!ServletFileUpload.isMultipartContent(request)) {
			response.getWriter().println("Does not support!");
			return;
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();

		factory.setSizeThreshold(THRESHOLD_SIZE);

		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);

		upload.setFileSizeMax(MAX_FILE_SIZE);

		upload.setSizeMax(REQUEST_SIZE);

		String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;

		File uploadDir = new File(uploadPath);
		if(!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		try {
			@SuppressWarnings("unchecked")
			List<FileItem> formItems = upload.parseRequest(request);
			
			System.out.println("Count item : " + formItems.size());
			
			if (formItems != null && formItems.size() > 0) {
				for (FileItem item : formItems) {
					if(!item.isFormField() && !item.getName().equalsIgnoreCase("")) {
						String fileName = new File(item.getName()).getName();
						String filePath = uploadPath + File.separator + fileName;
						File storeFile = new File(filePath);
						
						item.write(storeFile);
					}
				}

			}
			request.setAttribute("message", "Upload has been done successfully!");
		} catch (Exception e) {
			request.setAttribute("message", "There was an error: " + e.getMessage());
			e.printStackTrace();
		}
		getServletContext().getRequestDispatcher("/MessageServlet.jsp").forward(request, response);
	}

}
