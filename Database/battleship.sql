-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th12 31, 2017 lúc 11:48 AM
-- Phiên bản máy phục vụ: 10.1.26-MariaDB
-- Phiên bản PHP: 7.1.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `battleship`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `nguoichoi`
--

CREATE TABLE `nguoichoi` (
  `Email` text COLLATE utf8_vietnamese_ci NOT NULL,
  `TenNhanVat` text COLLATE utf8_vietnamese_ci NOT NULL,
  `MatKhau` text COLLATE utf8_vietnamese_ci NOT NULL,
  `Cap` text COLLATE utf8_vietnamese_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_vietnamese_ci;

--
-- Đang đổ dữ liệu cho bảng `nguoichoi`
--

INSERT INTO `nguoichoi` (`Email`, `TenNhanVat`, `MatKhau`, `Cap`) VALUES
('admin', 'Admin', '123', 'Tân binh'),
('vanha', 'Thehap Rok', '123', 'Cao thủ'),
('vancuong', 'Cường Đoàn', '123', 'Cao Thủ'),
('nhatchimai', 'Mai Già', 'maixd', 'Cao thủ'),
('thienthanh', 'Thiến', 'thien', 'Cao thủ');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
