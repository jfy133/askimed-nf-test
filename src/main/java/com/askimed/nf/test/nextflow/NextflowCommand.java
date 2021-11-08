package com.askimed.nf.test.nextflow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.askimed.nf.test.util.BinaryFinder;
import com.askimed.nf.test.util.Command;

import groovy.json.JsonOutput;

public class NextflowCommand {

	private String binary;

	private String script;

	private String profile;

	private File config;

	private File out;

	private boolean silent = true;

	private File trace = null;

	private Map<String, Object> params;

	public static String ERROR = "Nextflow Binary not found. Please check if Nextflow is in a directory accessible by your $PATH variable or set $NEXTFLOW_HOME.";

	public NextflowCommand() {
		binary = new BinaryFinder("nextflow").env("NEXTFLOW_HOME").envPath().path("/usr/local/bin").find();
	}

	public boolean isInstalled() {
		return binary != null;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public void setConfig(File config) {
		this.config = config;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public void setTrace(File trace) {
		this.trace = trace;
	}

	public File getTrace() {
		return trace;
	}

	public void setOut(File out) {
		this.out = out;
	}

	public File getOut() {
		return out;
	}

	public int execute() throws IOException {

		if (binary == null) {
			throw new IOException(ERROR);
		}

		File paramsFile = File.createTempFile("params", ".json");
		paramsFile.deleteOnExit();

		writeParamsJson(params, paramsFile);

		List<String> args = new Vector<String>();
		args.add("-quiet");
		args.add("run");
		args.add(script);
		if (config != null) {
			args.add("-c");
			args.add(config.getAbsolutePath());
		}
		args.add("-params-file");
		args.add(paramsFile.getAbsolutePath());
		args.add("-ansi-log");
		args.add("false");
		if (profile != null) {
			args.add("-profile");
			args.add(profile);
		}
		if (trace != null) {
			args.add("-with-trace");
			args.add(trace.getAbsolutePath());
		}
		Command nextflow = new Command(binary);
		nextflow.setParams(args);
		nextflow.setSilent(silent);
		if (out != null) {
			nextflow.saveStdOut(out.getAbsolutePath());
		}
		if (!silent) {
			System.out.println("Command: " + nextflow.getExecutedCommand());
		}
		return nextflow.execute();

	}

	public int printVersion() throws IOException {

		if (binary == null) {
			throw new IOException(ERROR);
		}

		List<String> args = new Vector<String>();
		args.add("-version");

		Command nextflow = new Command(binary);
		nextflow.setParams(args);
		nextflow.setSilent(false);

		return nextflow.execute();

	}

	protected void writeParamsJson(Map<String, Object> params, File paramsFile) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(paramsFile));
		writer.write(JsonOutput.toJson(params));
		writer.close();

	}

}
