package com.futotan.barcodeez.utils;

import com.google.zxing.*;


public class Barcode {
    public static Result decode(BinaryBitmap image) throws NotFoundException {
        // 自动识别类型
        MultiFormatReader reader = new MultiFormatReader();
        return reader.decode(image);
    }

    // 获取详细信息 比如 纠错等级，编码方式，版本，掩码模式
    public static String getDetailedInfo(Result result) {
        StringBuilder stringBuilder = new StringBuilder();
        
        switch (result.getBarcodeFormat()) {
            case QR_CODE:
                stringBuilder.append("Format: QR Code\n");
                break;
            case UPC_A:
                stringBuilder.append("Format: UPC-A\n");
                break;
            case UPC_E:
                stringBuilder.append("Format: UPC-E\n");
                break;
            case EAN_8:
                stringBuilder.append("Format: EAN-8\n");
                break;
            case EAN_13:
                stringBuilder.append("Format: EAN-13\n");
                break;
            case CODE_39:
                stringBuilder.append("Format: Code 39\n");
                break;
            case CODE_93:
                stringBuilder.append("Format: Code 93\n");
                break;
            case CODE_128:
                stringBuilder.append("Format: Code 128\n");
                break;
            case ITF:
                stringBuilder.append("Format: ITF\n");
                break;
            case PDF_417:
                stringBuilder.append("Format: PDF 417\n");
                break;
            case CODABAR:
                stringBuilder.append("Format: Codabar\n");
                break;
            case DATA_MATRIX:
                stringBuilder.append("Format: Data Matrix\n");
                break;
            case AZTEC:
                stringBuilder.append("Format: Aztec\n");
                break;
            case MAXICODE:
                stringBuilder.append("Format: MaxiCode\n");
                break;
            case RSS_14:
                stringBuilder.append("Format: RSS 14\n");
                break;
            case RSS_EXPANDED:
                stringBuilder.append("Format: RSS Expanded\n");
                break;
            case UPC_EAN_EXTENSION:
                stringBuilder.append("Format: UPC/EAN Extension\n");
                break;
        }

        result.getResultMetadata().forEach((key, value) -> {
            switch (key) {
                case ERROR_CORRECTION_LEVEL:
                    stringBuilder.append("Error Correction Level: ").append(value).append("\n");
                    break;
                case BYTE_SEGMENTS:
                    stringBuilder.append("Byte Segments: ").append(value).append("\n");
                    break;
                case SYMBOLOGY_IDENTIFIER:
                    stringBuilder.append("Symbology Identifier: ").append(value).append("\n");
                    break;
                case STRUCTURED_APPEND_SEQUENCE:
                    stringBuilder.append("Structured Append Sequence: ").append(value).append("\n");
                    break;
                case STRUCTURED_APPEND_PARITY:
                    stringBuilder.append("Structured Append Parity: ").append(value).append("\n");
                    break;
                case SUGGESTED_PRICE:
                    stringBuilder.append("Suggested Price: ").append(value).append("\n");
                    break;
                case POSSIBLE_COUNTRY:
                    stringBuilder.append("Possible Country: ").append(value).append("\n");
                    break;
                case UPC_EAN_EXTENSION:
                    stringBuilder.append("UPC/EAN Extension: ").append(value).append("\n");
                    break;
                case PDF417_EXTRA_METADATA:
                    stringBuilder.append("PDF417 Extra Metadata: ").append(value).append("\n");
                    break;
                case ORIENTATION:
                    stringBuilder.append("Orientation: ").append(value).append("\n");
                    break;
            }
        });

        return stringBuilder.toString();
    }
}
