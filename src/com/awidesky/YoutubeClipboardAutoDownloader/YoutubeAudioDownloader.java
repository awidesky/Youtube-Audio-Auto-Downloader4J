package com.awidesky.YoutubeClipboardAutoDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class YoutubeAudioDownloader {

	private static final String projectpath = new File(
			YoutubeAudioDownloader.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
	private static final String youtubedlpath = projectpath + "\\YoutubeAudioAutoDownloader-resources\\ffmpeg\\bin";
	private static File downloadPath;
	private static Pattern pattern = Pattern.compile("^[0-9]+%$");
	
	static void checkFiles() {
		// System.out.println(youtubedlpath);
		if (!new File(youtubedlpath + "\\youtube-dl.exe").exists()) {
			throw new Error("youtube-dl.exe does not exist in " + youtubedlpath);

		}
	}

	public static String getProjectpath() {
		return projectpath;
	}

	static void download(String url, TaskStatusModel task) throws Exception {

		downloadPath = new File(Main.getProperties().getSaveto());

		try {

			task.setDest(Main.getProperties().getSaveto());
			
			
			/* get video name */
			ProcessBuilder pbGetName = new ProcessBuilder(youtubedlpath + "\\youtube-dl.exe", "--get-filename -o \"%(title)s\"", url);
			Process p1 = pbGetName.directory(null).start();
			BufferedReader br1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
			task.setVideoName(br1.readLine());
			p1.waitFor();
			
			
			/* download video */
			ProcessBuilder pb = new ProcessBuilder(youtubedlpath + "\\youtube-dl.exe", "-x",
					"-o" + "\"%(title)s.%(ext)s\"", Main.getProperties().getPlaylistOption(), "--audio-format",
					Main.getProperties().getFormat(), "--audio-quality", Main.getProperties().getQuality(), url);
			Process p = pb.directory(downloadPath).start();

			task.setStatus("Downloading");
			
			Thread stdout = new Thread(() -> {

				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = null;

				
				try {

					while ((line = br.readLine()) != null) {

						if (line.startsWith("[download]")) {
							
							task.setProgress(Integer.parseInt(pattern.matcher(line).group().replace("%", "")));
							
						}
						
						Main.log(line);

					}

				} catch (IOException e) {

					Main.log(e.toString());

				}

			});

			
			Thread stderr = new Thread(() -> {

				BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = null;
				StringBuilder sb = new StringBuilder("");
				
				try {

					while ((line = br.readLine()) != null) {
						
						task.setStatus("ERROR");
						sb.append(line);

					}
					
					if (!sb.toString().equals("")) throw new RuntimeException(sb.toString());

				} catch (IOException e) {

					// Main.log(e.toString());

				}
				

			});

			stdout.start();
			stderr.start();

			p.waitFor();
			
			task.done();

			// Thread.currentThread().sleep(100);
			/*
			 * Main.log("Finding downloaded file...");
			 * 
			 * File[] fileList = new File(youtubedlpath).listFiles(new FilenameFilter() {
			 * 
			 * @Override public boolean accept(File dir, String name) { return
			 * name.endsWith(Main.getProperties().getFormat()); }
			 * 
			 * });
			 * 
			 * if(fileList.length ==0 ) { throw new
			 * RuntimeException("Youtube-dl didn't dowload any files!"); }
			 * 
			 * for(File f : fileList) {
			 * 
			 * Files.copy(f.toPath(), Paths.get(downloadPath.getAbsolutePath() + "\\" +
			 * f.getName()) ,StandardCopyOption.REPLACE_EXISTING); Files.delete(f.toPath());
			 * 
			 * }
			 */
			Main.log("Done!\n");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
