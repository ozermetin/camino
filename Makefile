# Make file for the camino applications.
# 
# $Id: Makefile,v 1.31 2006/08/10 14:33:12 ucacpco Exp $


all: 				AddNoise AnalyzeHeader BinaryToText CP_Stats ClassifiedModelFit ConnectivityMatrix ConsistencyFraction CountSeeds DataStats DeconvToCamenoFormat DT_EigenSystem DT_FitMatrix DT_ShapeStatistics DT_ToCamino DT_ToImage ElectrostaticSubsets EntryPoint EstimateSNR FSL_ToScheme F_TestThresholdSelector FanningGrid FracAnis GatherStats MeanDiff MESD ModelFit ImageMath ImageToVoxel InversionStats LinearRecon MultiFibreReconStats OrderElectrostaticPoints OrientationBiasMap PICoApps PointSetToScheme ProcessStreamlines QBallMX Reorient RGB_ScalarImage ScannerToVoxel SchemeToFSL SequenceStats Shredder Simulation Split4D_Nii SphFuncAnisotropy SphFuncSkewness SphFuncKurtosis SphFuncBitMap SphHarmFitter SphFuncPD_Stats SphPDF_Fit StreamlineTractography SubsetScheme SyntheticData TargetProbsToASCII TensorOrientationViewer ThresholdB0 TraceD TractCounter TractShredder TractStatistics VoxelClassify VoxelMean VoxelToImage VoxelToScanner VoxelwiseImageStats VTK_Streamlines WriteZeros MatlabArrayDataSource jar 

# Builds everything not in the test directory
allclasses::			
				javac -classpath . `find . -name "*.java" | grep -v \./test`			

AddNoise:			apps/AddNoise.class
apps/AddNoise.class:		apps/AddNoise.java
				javac -classpath . apps/AddNoise.java

AnalyzeHeader:			imaging/AnalyzeHeader.class
imaging/AnalyzeHeader.class:	imaging/AnalyzeHeader.java
				javac -classpath . imaging/AnalyzeHeader.java

BinaryToText:			apps/BinaryToText.class

apps/BinaryToText.class: 	apps/BinaryToText.java
				javac -classpath . apps/BinaryToText.java

ClassifiedModelFit:		apps/ClassifiedModelFit.class

apps/ClassifiedModelFit.class: apps/ClassifiedModelFit.java
				javac -classpath . apps/ClassifiedModelFit.java

ConnectivityMatrix:		apps/ConnectivityMatrix.class

apps/ConnectivityMatrix.class: apps/ConnectivityMatrix.java
				javac -classpath . apps/ConnectivityMatrix.java

ConsistencyFraction:		apps/ConsistencyFraction.class

apps/ConsistencyFraction.class: apps/ConsistencyFraction.java
				javac -classpath . apps/ConsistencyFraction.java

CountSeeds:			apps/CountSeeds.class

apps/CountSeeds.class:		apps/CountSeeds.java
				javac -classpath . apps/CountSeeds.java


CP_Stats:			apps/CP_Stats.class

apps/CP_Stats.class:		apps/CP_Stats.java
				javac -classpath . apps/CP_Stats.java	


DataStats:			apps/DataStats.class

apps/DataStats.class:		apps/DataStats.java
				javac -classpath . apps/DataStats.java	


DeconvToCamenoFormat:		apps/DeconvToCamenoFormat.class

apps/DeconvToCamenoFormat.class: apps/DeconvToCamenoFormat.java
				javac -classpath . apps/DeconvToCamenoFormat.java

DT_EigenSystem:			apps/DT_EigenSystem.class

apps/DT_EigenSystem.class: 	apps/DT_EigenSystem.java
				javac -classpath . apps/DT_EigenSystem.java

DT_FitMatrix:			apps/DT_FitMatrix.class

apps/DT_FitMatrix.class: 	apps/DT_FitMatrix.java
				javac -classpath . apps/DT_FitMatrix.java

DT_ShapeStatistics:		apps/DT_ShapeStatistics.class

apps/DT_ShapeStatistics.class: 	apps/DT_ShapeStatistics.java
				javac -classpath . apps/DT_ShapeStatistics.java

DT_ToCamino:			apps/DT_ToCamino.class

apps/DT_ToCamino.class:		apps/DT_ToCamino.java
				javac -classpath . apps/DT_ToCamino.java

DT_ToImage:			apps/DT_ToImage.class

apps/DT_ToImage.class:		apps/DT_ToImage.java
				javac -classpath . apps/DT_ToImage.java


ElectrostaticSubsets:		apps/ElectrostaticSubsets.class

apps/ElectrostaticSubsets.class:	apps/ElectrostaticSubsets.java
				javac -classpath . apps/ElectrostaticSubsets.java

EntryPoint:			apps/EntryPoint.class
apps/EntryPoint.class:		apps/EntryPoint.java
				javac -classpath . apps/EntryPoint.java

EstimateSNR:			apps/EstimateSNR.class
apps/EstimateSNR.class:		apps/EstimateSNR.java
				javac -classpath . apps/EstimateSNR.java

F_TestThresholdSelector:	apps/F_TestThresholdSelector.class

apps/F_TestThresholdSelector.class: apps/F_TestThresholdSelector.java
				javac -classpath . apps/F_TestThresholdSelector.java

FanningGrid:    apps/FanningGrid.class

apps/FanningGrid.class:		apps/FanningGrid.java
				javac -classpath . apps/FanningGrid.java

FracAnis:			apps/FracAnis.class

apps/FracAnis.class: 		apps/FracAnis.java
				javac -classpath . apps/FracAnis.java

FSL_ToScheme:			apps/FSL_ToScheme.class

apps/FSL_ToScheme.class: 	apps/FSL_ToScheme.java
				javac -classpath . apps/FSL_ToScheme.java

GatherStats:			apps/GatherStats.class

apps/GatherStats.class:		apps/GatherStats.java
				javac -classpath . apps/GatherStats.java

ImageMath:			apps/ImageMath.class

apps/ImageMath.class:		apps/ImageMath.java
				javac -classpath . apps/ImageMath.java

ImageToVoxel:			apps/ImageToVoxel.class

apps/ImageToVoxel.class:	apps/ImageToVoxel.java
				javac -classpath . apps/ImageToVoxel.java

MeanDiff:			apps/MeanDiff.class

apps/MeanDiff.class:		apps/MeanDiff.java	
				javac -classpath . apps/MeanDiff.java

MESD:				apps/MESD.class

apps/MESD.class:		apps/MESD.java	
				javac -classpath . apps/MESD.java

ModelFit:			apps/ModelFit.class

apps/ModelFit.class:		apps/ModelFit.java	
				javac -classpath . apps/ModelFit.java

InversionStats:			apps/InversionStats.class

apps/InversionStats.class:	apps/InversionStats.java
				javac -classpath . apps/InversionStats.java

LinearRecon:			apps/LinearRecon.class

apps/LinearRecon.class:		apps/LinearRecon.java
				javac -classpath . apps/LinearRecon.java

MultiFibreReconStats:		apps/MultiFibreReconStats.class

apps/MultiFibreReconStats.class:	apps/MultiFibreReconStats.java
				javac -classpath . apps/MultiFibreReconStats.java

OrderElectrostaticPoints:		apps/OrderElectrostaticPoints.class

apps/OrderElectrostaticPoints.class:	apps/OrderElectrostaticPoints.java
				javac -classpath . apps/OrderElectrostaticPoints.java

OrientationBiasMap:		apps/OrientationBiasMap.class

apps/OrientationBiasMap.class:	apps/OrientationBiasMap.java
				javac -classpath . apps/OrientationBiasMap.java

PICoApps:			apps/GenerateDTLUT.class apps/CombineTwoFibreLUTs.class apps/PICoPDFs.class apps/GenerateMFR_LUT.class apps/SphFuncPICoCalibrationData.class

apps/GenerateDTLUT.class:	apps/GenerateDTLUT.java
				javac -classpath . apps/GenerateDTLUT.java

apps/SphFuncPICoCalibrationData.class:	apps/SphFuncPICoCalibrationData.java
				javac -classpath . apps/SphFuncPICoCalibrationData.java

apps/GenerateMFR_LUT.class:	apps/GenerateMFR_LUT.java
				javac -classpath . apps/GenerateMFR_LUT.java


apps/CombineTwoFibreLUTs.class:	apps/CombineTwoFibreLUTs.java
				javac -classpath . apps/CombineTwoFibreLUTs.java

apps/PICoPDFs.class:		apps/PICoPDFs.java
				javac -classpath . apps/PICoPDFs.java

PointSetToScheme:		apps/PointSetToScheme.class

apps/PointSetToScheme.class:	apps/PointSetToScheme.java
				javac -classpath . apps/PointSetToScheme.java

ProcessStreamlines:		apps/ProcessStreamlines.class

apps/ProcessStreamlines.class:	apps/ProcessStreamlines.java
				javac -classpath . apps/ProcessStreamlines.java

QBallMX:			apps/QBallMX.class

apps/QBallMX.class:		apps/QBallMX.java
				javac -classpath . apps/QBallMX.java

Reorient:			apps/Reorient.class

apps/Reorient.class:		apps/Reorient.java
				javac -classpath . apps/Reorient.java

RGB_ScalarImage:		apps/RGB_ScalarImage.class

apps/RGB_ScalarImage.class:	apps/RGB_ScalarImage.java
				javac -classpath . apps/RGB_ScalarImage.java


ScannerToVoxel:			apps/ScannerToVoxel.class

apps/ScannerToVoxel.class:	apps/ScannerToVoxel.java
				javac -classpath . apps/ScannerToVoxel.java

SchemeToFSL:			apps/SchemeToFSL.class

apps/SchemeToFSL.class:		apps/SchemeToFSL.java
				javac -classpath . apps/SchemeToFSL.java


SequenceStats:			apps/SequenceStats.class

apps/SequenceStats.class:	apps/SequenceStats.java
				javac -classpath . apps/SequenceStats.java

Shredder:			apps/Shredder.class
apps/Shredder.class:		apps/Shredder.java
				javac -classpath . apps/Shredder.java

Simulation:			apps/SyntheticData.class
				javac -classpath . simulation/*.java

Split4D_Nii:			apps/Split4D_NiiImage.class
apps/Split4D_NiiImage.class:	apps/Split4D_NiiImage.java
				javac -classpath . apps/Split4D_NiiImage.java


SphFuncAnisotropy:		apps/SphFuncAnisotropy.class

apps/SphFuncAnisotropy.class:	apps/SphFuncAnisotropy.java
				javac -classpath . apps/SphFuncAnisotropy.java

SphFuncSkewness:		apps/SphFuncSkewness.class

apps/SphFuncSkewness.class:	apps/SphFuncSkewness.java
				javac -classpath . apps/SphFuncSkewness.java

SphFuncKurtosis:		apps/SphFuncKurtosis.class

apps/SphFuncKurtosis.class:	apps/SphFuncKurtosis.java
				javac -classpath . apps/SphFuncKurtosis.java

SphFuncBitMap:			apps/SphFuncBitMap.class

apps/SphFuncBitMap.class:	apps/SphFuncBitMap.java
				javac -classpath . apps/SphFuncBitMap.java

SphHarmFitter:			apps/SphHarmFitter.class

apps/SphHarmFitter.class:	apps/SphHarmFitter.java
				javac -classpath . apps/SphHarmFitter.java

SphFuncPD_Stats:		apps/SphFuncPD_Stats.class

apps/SphFuncPD_Stats.class:	apps/SphFuncPD_Stats.java
				javac -classpath . apps/SphFuncPD_Stats.java

SphPDF_Fit:			apps/SphPDF_Fit.class

apps/SphPDF_Fit.class:		apps/SphPDF_Fit.java
				javac -classpath . apps/SphPDF_Fit.java


StreamlineTractography:		apps/StreamlineTractography.class
apps/StreamlineTractography.class:	apps/StreamlineTractography.java
					javac -classpath . apps/StreamlineTractography.java


SubsetScheme:		        apps/SubsetScheme.class

apps/SubsetScheme.class:	apps/SubsetScheme.java
				javac -classpath . apps/SubsetScheme.java


SyntheticData:		        apps/SyntheticData.class

apps/SyntheticData.class:	apps/SyntheticData.java
				javac -classpath . apps/SyntheticData.java

TargetProbsToASCII:		apps/TargetProbsToASCII.class

apps/TargetProbsToASCII.class: apps/TargetProbsToASCII.java
				javac -classpath . apps/TargetProbsToASCII.java


TensorOrientationViewer:	apps/PD_OrientationViewer.class

apps/PD_OrientationViewer.class: apps/PD_OrientationViewer.java
				javac -classpath . apps/PD_OrientationViewer.java

ThresholdB0:			apps/ThresholdB0.class

apps/ThresholdB0.class: 	apps/ThresholdB0.java
				javac -classpath . apps/ThresholdB0.java

TractCounter:			apps/TractCounter.class

apps/TractCounter.class: 	apps/TractCounter.java
				javac -classpath . apps/TractCounter.java

TractShredder:			apps/TractShredder.class

apps/TractShredder.class: 	apps/TractShredder.java
				javac -classpath . apps/TractShredder.java

TractStatistics:		apps/TractStatistics.class

apps/TractStatistics.class: 		apps/TractStatistics.java
				javac -classpath . apps/TractStatistics.java


TraceD:				apps/TraceD.class

apps/TraceD.class: 		apps/TraceD.java
				javac -classpath . apps/TraceD.java

VoxelClassify:			apps/VoxelClassify.class

apps/VoxelClassify.class:	apps/VoxelClassify.java
				javac -classpath . apps/VoxelClassify.java

VoxelMean:			apps/VoxelMean.class

apps/VoxelMean.class:		apps/VoxelMean.java
				javac -classpath . apps/VoxelMean.java

VoxelToImage:			apps/VoxelToImage.class

apps/VoxelToImage.class:	apps/VoxelToImage.java
				javac -classpath . apps/VoxelToImage.java

VoxelToScanner:			apps/VoxelToScanner.class

apps/VoxelToScanner.class:	apps/VoxelToScanner.java
				javac -classpath . apps/VoxelToScanner.java

VoxelwiseImageStats:		apps/VoxelwiseImageStats.class

apps/VoxelwiseImageStats.class:	apps/VoxelwiseImageStats.java
				javac -classpath . apps/VoxelwiseImageStats.java

VTK_Streamlines:		apps/VTK_Streamlines.class

apps/VTK_Streamlines.class:	apps/VTK_Streamlines.java
				javac -classpath . apps/VTK_Streamlines.java

WriteZeros:			apps/WriteZeros.class

apps/WriteZeros.class:		apps/WriteZeros.java
				javac -classpath . apps/WriteZeros.java


MatlabArrayDataSource:		data/MatlabArrayDataSource.class

data/MatlabArrayDataSource.class:		data/MatlabArrayDataSource.java
				javac -classpath . data/MatlabArrayDataSource.java


docs:
				javadoc -d ./javadoc Jama apps data imaging inverters linearHARDI linearReconstruction mesd misc numerics optimizers simulation sphfunc tools tractography			
manhtml:			
				doc/makeManHTML.pl

jar:
				jar cf camino.jar */*.class */*/*.class


cleandocs:
				rm -rf javadoc

clean:
				rm -f `find . -name "*~"` `find . -name "*\.class"`
