UPDATE image i
JOIN product p ON p.product_id = i.product_id
JOIN (
    SELECT 'ASUS ROG Throne Qi' AS product_name, '/image/ASUS ROG Throne Qi.png' AS image_url UNION ALL
    SELECT 'Cooler Master MasterAccessory GEM' AS product_name, '/image/Cooler Master MasterAccessory GEM.jpg' AS image_url UNION ALL
    SELECT 'Corsair ST100 RGB Premium Headset Stand' AS product_name, '/image/Corsair ST100 RGB Premium Headset Stand.jpg' AS image_url UNION ALL
    SELECT 'Elgato Key Light Air' AS product_name, '/image/Elgato Key Light Air.jpg' AS image_url UNION ALL
    SELECT 'Elgato Stream Deck MK.2' AS product_name, '/image/Elgato Stream Deck MK.2.jpg' AS image_url UNION ALL
    SELECT 'Elgato Wave XLR' AS product_name, '/image/Elgato Wave XLR.jpg' AS image_url UNION ALL
    SELECT 'Logitech Litra Beam' AS product_name, '/image/Logitech Litra Beam.jpg' AS image_url UNION ALL
    SELECT 'Razer Base Station V2 Chroma' AS product_name, '/image/Razer Base Station V2 Chroma.jpg' AS image_url UNION ALL
    SELECT 'Razer Mouse Dock Pro' AS product_name, '/image/Razer Mouse Dock Pro.jpg' AS image_url UNION ALL
    SELECT 'SteelSeries Arena Wireless Mic' AS product_name, '/image/SteelSeries Arena Wireless Mic.jpg' AS image_url UNION ALL
    SELECT 'Cooler Master Caliber X2' AS product_name, '/image/Cooler Master Caliber X2.jpg' AS image_url UNION ALL
    SELECT 'Cooler Master Hybrid 1 Ergo' AS product_name, '/image/Cooler Master Hybrid 1 Ergo.jpg' AS image_url UNION ALL
    SELECT 'Corsair TC100 Relaxed Fabric' AS product_name, '/image/Corsair TC100 Relaxed Fabric.jpg' AS image_url UNION ALL
    SELECT 'Corsair TC200 Fabric' AS product_name, '/image/Corsair TC200 Fabric.jpg' AS image_url UNION ALL
    SELECT 'MSI MAG CH130 I Repeltek Fabric' AS product_name, '/image/MSI MAG CH130 I Repeltek Fabric.jpg' AS image_url UNION ALL
    SELECT 'Razer Fujin' AS product_name, '/image/Razer Fujin.jpg' AS image_url UNION ALL
    SELECT 'Razer Iskur V2' AS product_name, '/image/Razer Iskur V2.jpg' AS image_url UNION ALL
    SELECT 'Secretlab TITAN Evo Regular' AS product_name, '/image/Secretlab TITAN Evo Regular.jpg' AS image_url UNION ALL
    SELECT 'Secretlab TITAN Evo XL' AS product_name, '/image/Secretlab TITAN Evo XL.jpg' AS image_url UNION ALL
    SELECT 'Thermaltake Argent E700' AS product_name, '/image/Thermaltake Argent E700.jpg' AS image_url UNION ALL
    SELECT 'ASUS ROG Raikiri Pro' AS product_name, '/image/ASUS ROG Raikiri Pro.jpg' AS image_url UNION ALL
    SELECT 'Cooler Master Storm Controller' AS product_name, '/image/Cooler Master Storm Controller.jpg' AS image_url UNION ALL
    SELECT 'HyperX Clutch Gladiate RGB' AS product_name, '/image/HyperX Clutch Gladiate RGB.jpg' AS image_url UNION ALL
    SELECT 'Logitech F310 Gamepad' AS product_name, '/image/Logitech F310 Gamepad.jpg' AS image_url UNION ALL
    SELECT 'Microsoft Xbox Elite Wireless Controller Series 2' AS product_name, '/image/Microsoft Xbox Elite Wireless Controller Series 2.jpg' AS image_url UNION ALL
    SELECT 'Microsoft Xbox Wireless Controller Carbon Black' AS product_name, '/image/Microsoft Xbox Wireless Controller Carbon Black.jpg' AS image_url UNION ALL
    SELECT 'MSI Force GC30 V2 White' AS product_name, '/image/MSI Force GC30 V2 White.jpg' AS image_url UNION ALL
    SELECT 'Razer Wolverine V2 Chroma' AS product_name, '/image/Razer Wolverine V2 Chroma.jpg' AS image_url UNION ALL
    SELECT 'Razer Wolverine V3 Pro' AS product_name, '/image/Razer Wolverine V3 Pro.jpg' AS image_url UNION ALL
    SELECT 'SteelSeries Stratus Duo' AS product_name, '/image/SteelSeries Stratus Duo.jpg' AS image_url UNION ALL
    SELECT 'Arctic Liquid Freezer III 360 A-RGB' AS product_name, '/image/Arctic Liquid Freezer III 360 A-RGB.jpg' AS image_url UNION ALL
    SELECT 'AMD Ryzen 5 9600X' AS product_name, '/image/AMD Ryzen 5 9600X.jpg' AS image_url UNION ALL
    SELECT 'AMD Ryzen 7 9700X' AS product_name, '/image/AMD Ryzen 7 9700X.jpg' AS image_url UNION ALL
    SELECT 'Cooler Master GD120 ARGB Desk' AS product_name, '/image/Cooler Master GD120 ARGB Desk.jpg' AS image_url UNION ALL
    SELECT 'Cooler Master GD160 ARGB Gaming Desk' AS product_name, '/image/Cooler Master GD160 ARGB Gaming Desk.jpg' AS image_url UNION ALL
    SELECT 'Razer Iskur Desk Mat Bundle' AS product_name, '/image/Razer Iskur Desk Mat Bundle.jpg' AS image_url UNION ALL
    SELECT 'Secretlab MAGNUS Metal Desk' AS product_name, '/image/Secretlab MAGNUS Metal Desk.jpg' AS image_url UNION ALL
    SELECT 'Secretlab MAGNUS Pro' AS product_name, '/image/Secretlab MAGNUS Pro.jpg' AS image_url UNION ALL
    SELECT 'Thermaltake Level 20 BattleStation' AS product_name, '/image/Thermaltake Level 20 BattleStation.jpg' AS image_url UNION ALL
    SELECT 'Thermaltake ToughDesk 300 RGB' AS product_name, '/image/Thermaltake ToughDesk 300 RGB.jpg' AS image_url UNION ALL
    SELECT 'ASUS ROG Delta S Wireless' AS product_name, '/image/ASUS ROG Delta S Wireless.jpg' AS image_url UNION ALL
    SELECT 'Corsair HS80 Max Wireless' AS product_name, '/image/Corsair HS80 Max Wireless.jpg' AS image_url UNION ALL
    SELECT 'HyperX Cloud Alpha Wireless' AS product_name, '/image/HyperX Cloud Alpha Wireless.jpg' AS image_url UNION ALL
    SELECT 'HyperX Cloud III Wireless' AS product_name, '/image/HyperX Cloud III Wireless.jpg' AS image_url UNION ALL
    SELECT 'Logitech G PRO X 2 Lightspeed' AS product_name, '/image/Logitech G PRO X 2 Lightspeed.jpg' AS image_url UNION ALL
    SELECT 'Logitech G733 Lightspeed' AS product_name, '/image/Logitech G733 Lightspeed.jpg' AS image_url UNION ALL
    SELECT 'Razer Barracuda X 2022' AS product_name, '/image/Razer Barracuda X 2022.jpg' AS image_url UNION ALL
    SELECT 'Razer BlackShark V2 Pro 2023' AS product_name, '/image/Razer BlackShark V2 Pro 2023.jpg' AS image_url UNION ALL
    SELECT 'SteelSeries Arctis Nova 5 Wireless' AS product_name, '/image/SteelSeries Arctis Nova 5 Wireless.jpg' AS image_url UNION ALL
    SELECT 'SteelSeries Arctis Nova Pro Wireless' AS product_name, '/image/SteelSeries Arctis Nova Pro Wireless.jpg' AS image_url UNION ALL
    SELECT 'Akko MOD 007B HE' AS product_name, '/image/Akko MOD 007B HE.jpg' AS image_url UNION ALL
    SELECT 'ASUS ROG Azoth' AS product_name, '/image/ASUS ROG Azoth.jpg' AS image_url UNION ALL
    SELECT 'Corsair K70 RGB Pro' AS product_name, '/image/Corsair K70 RGB Pro.jpg' AS image_url UNION ALL
    SELECT 'Keychron K2 HE' AS product_name, '/image/Keychron K2 HE.jpg' AS image_url UNION ALL
    SELECT 'Keychron Q1 Max' AS product_name, '/image/Keychron Q1 Max.jpg' AS image_url UNION ALL
    SELECT 'Logitech G915 X Lightspeed TKL' AS product_name, '/image/Logitech G915 X Lightspeed TKL.jpg' AS image_url UNION ALL
    SELECT 'Razer BlackWidow V4 75' AS product_name, '/image/Razer BlackWidow V4 75.jpg' AS image_url UNION ALL
    SELECT 'Razer Huntsman V3 Pro TKL' AS product_name, '/image/Razer Huntsman V3 Pro TKL.jpg' AS image_url UNION ALL
    SELECT 'SteelSeries Apex Pro TKL Wireless' AS product_name, '/image/SteelSeries Apex Pro TKL Wireless.jpg' AS image_url UNION ALL
    SELECT 'Corsair Vengeance 32GB (2x16GB) DDR5 6000MHz CL30 EXPO' AS product_name, '/image/Corsair Vengeance 32GB (2x16GB) DDR5 6000MHz CL30 EXPO.jpg' AS image_url UNION ALL
    SELECT 'G.Skill Flare X5 32GB (2x16GB) DDR5 6000MHz CL36' AS product_name, '/image/G.Skill Flare X5 32GB (2x16GB) DDR5 6000MHz CL36.jpg' AS image_url UNION ALL
    SELECT 'Blue Yeti Nano' AS product_name, '/image/Blue Yeti Nano.jpg' AS image_url UNION ALL
    SELECT 'Blue Yeti USB Microphone' AS product_name, '/image/Blue Yeti USB Microphone.jpg' AS image_url UNION ALL
    SELECT 'Creative Live Mic M3' AS product_name, '/image/Creative Live Mic M3.jpg' AS image_url UNION ALL
    SELECT 'Elgato Wave 3' AS product_name, '/image/Elgato Wave 3.jpg' AS image_url UNION ALL
    SELECT 'Elgato Wave DX' AS product_name, '/image/Elgato Wave DX.jpg' AS image_url UNION ALL
    SELECT 'HyperX QuadCast S RGB' AS product_name, '/image/HyperX QuadCast S RGB.jpg' AS image_url UNION ALL
    SELECT 'HyperX SoloCast' AS product_name, '/image/HyperX SoloCast.jpg' AS image_url UNION ALL
    SELECT 'Logitech Blue Sona' AS product_name, '/image/Logitech Blue Sona.jpg' AS image_url UNION ALL
    SELECT 'Razer Seiren Mini' AS product_name, '/image/Razer Seiren Mini.jpg' AS image_url UNION ALL
    SELECT 'Razer Seiren V2 X' AS product_name, '/image/Razer Seiren V2 X.jpg' AS image_url UNION ALL
    SELECT 'ASUS ProArt PA278CV' AS product_name, '/image/ASUS ProArt PA278CV.jpg' AS image_url UNION ALL
    SELECT 'ASUS TUF Gaming VG27AQ3A' AS product_name, '/image/ASUS TUF Gaming VG27AQ3A.jpg' AS image_url UNION ALL
    SELECT 'Dell G2724D' AS product_name, '/image/Dell G2724D.jpg' AS image_url UNION ALL
    SELECT 'Gigabyte M27Q' AS product_name, '/image/Gigabyte M27Q.jpg' AS image_url UNION ALL
    SELECT 'LG UltraGear 27GP850-B' AS product_name, '/image/LG UltraGear 27GP850-B.jpg' AS image_url UNION ALL
    SELECT 'MSI G274QPF-QD' AS product_name, '/image/MSI G274QPF-QD.jpg' AS image_url UNION ALL
    SELECT 'MSI Modern MD272QXP' AS product_name, '/image/MSI Modern MD272QXP.jpg' AS image_url UNION ALL
    SELECT 'Samsung Odyssey G5 G55C 27' AS product_name, '/image/Samsung Odyssey G5 G55C 27.jpg' AS image_url UNION ALL
    SELECT 'ViewSonic VX2758A-2K-PRO' AS product_name, '/image/ViewSonic VX2758A-2K-PRO.jpg' AS image_url UNION ALL
    SELECT 'ASUS ROG Harpe Ace Aim Lab Edition' AS product_name, '/image/ASUS ROG Harpe Ace Aim Lab Edition.jpg' AS image_url UNION ALL
    SELECT 'Cooler Master MM712' AS product_name, '/image/Cooler Master MM712.jpg' AS image_url UNION ALL
    SELECT 'Corsair M75 Wireless' AS product_name, '/image/Corsair M75 Wireless.jpg' AS image_url UNION ALL
    SELECT 'HyperX Pulsefire Haste 2 Wireless' AS product_name, '/image/HyperX Pulsefire Haste 2 Wireless.jpg' AS image_url UNION ALL
    SELECT 'Logitech G502 X Lightspeed' AS product_name, '/image/Logitech G502 X Lightspeed.jpg' AS image_url UNION ALL
    SELECT 'Logitech MX Master 3S' AS product_name, '/image/Logitech MX Master 3S.jpg' AS image_url UNION ALL
    SELECT 'Razer Basilisk V3 Pro' AS product_name, '/image/Razer Basilisk V3 Pro.jpg' AS image_url UNION ALL
    SELECT 'Razer Viper V3 Pro' AS product_name, '/image/Razer Viper V3 Pro.jpg' AS image_url UNION ALL
    SELECT 'SteelSeries Aerox 3 Wireless' AS product_name, '/image/SteelSeries Aerox 3 Wireless.jpg' AS image_url UNION ALL
    SELECT 'SteelSeries Rival 5' AS product_name, '/image/SteelSeries Rival 5.jpg' AS image_url UNION ALL
    SELECT 'ASUS ROG Sheath BLK LTD' AS product_name, '/image/ASUS ROG Sheath BLK LTD.jpg' AS image_url UNION ALL
    SELECT 'Cooler Master MP511 XL' AS product_name, '/image/Cooler Master MP511 XL.jpg' AS image_url UNION ALL
    SELECT 'Corsair MM700 RGB Extended' AS product_name, '/image/Corsair MM700 RGB Extended.jpg' AS image_url UNION ALL
    SELECT 'HyperX Pulsefire Mat RGB XL' AS product_name, '/image/HyperX Pulsefire Mat RGB XL.jpg' AS image_url UNION ALL
    SELECT 'Logitech G Powerplay Wireless Charging Mousepad' AS product_name, '/image/Logitech G Powerplay Wireless Charging Mousepad.jpg' AS image_url UNION ALL
    SELECT 'Logitech G840 XL' AS product_name, '/image/Logitech G840 XL.jpg' AS image_url UNION ALL
    SELECT 'Razer Gigantus V2 XXL' AS product_name, '/image/Razer Gigantus V2 XXL.jpg' AS image_url UNION ALL
    SELECT 'Razer Strider Large' AS product_name, '/image/Razer Strider Large.jpg' AS image_url UNION ALL
    SELECT 'SteelSeries QcK Heavy XXL' AS product_name, '/image/SteelSeries QcK Heavy XXL.jpg' AS image_url UNION ALL
    SELECT 'SteelSeries QcK Prism Cloth XL' AS product_name, '/image/SteelSeries QcK Prism Cloth XL.jpg' AS image_url UNION ALL
    SELECT 'Corsair RM850e ATX 3.1 850W 80+ Gold' AS product_name, '/image/Corsair RM850e ATX 3.1 850W 80+ Gold.jpg' AS image_url UNION ALL
    SELECT 'Creative Pebble Pro' AS product_name, '/image/Creative Pebble Pro.jpg' AS image_url UNION ALL
    SELECT 'Creative Sound Blaster Katana SE' AS product_name, '/image/Creative Sound Blaster Katana SE.jpg' AS image_url UNION ALL
    SELECT 'Creative Stage Air V2' AS product_name, '/image/Creative Stage Air V2.jpg' AS image_url UNION ALL
    SELECT 'Edifier G2000 Gaming Speakers' AS product_name, '/image/Edifier G2000 Gaming Speakers.jpg' AS image_url UNION ALL
    SELECT 'Edifier MR4 Studio Monitor' AS product_name, '/image/Edifier MR4 Studio Monitor.jpg' AS image_url UNION ALL
    SELECT 'Logitech G560 Lightsync Speakers' AS product_name, '/image/Logitech G560 Lightsync Speakers.jpg' AS image_url UNION ALL
    SELECT 'Logitech Z407 Bluetooth Speakers' AS product_name, '/image/Logitech Z407 Bluetooth Speakers.jpg' AS image_url UNION ALL
    SELECT 'Logitech Z625 Speaker System' AS product_name, '/image/Logitech Z625 Speaker System.jpg' AS image_url UNION ALL
    SELECT 'Razer Leviathan V2 X' AS product_name, '/image/Razer Leviathan V2 X.jpg' AS image_url UNION ALL
    SELECT 'Razer Nommo V2 X' AS product_name, '/image/Razer Nommo V2 X.jpg' AS image_url UNION ALL
    SELECT 'Kingston NV3 1TB PCIe 4.0 NVMe' AS product_name, '/image/Kingston NV3 1TB PCIe 4.0 NVMe.jpg' AS image_url UNION ALL
    SELECT 'Kingston NV3 2TB PCIe 4.0 NVMe' AS product_name, '/image/Kingston NV3 2TB PCIe 4.0 NVMe.jpg' AS image_url UNION ALL
    SELECT 'Kingston NV3 4TB PCIe 4.0 NVMe' AS product_name, '/image/Kingston NV3 4TB PCIe 4.0 NVMe.jpg' AS image_url UNION ALL
    SELECT 'AOC AM420 Webcam' AS product_name, '/image/AOC AM420 Webcam.jpg' AS image_url UNION ALL
    SELECT 'ASUS ROG Eye S' AS product_name, '/image/ASUS ROG Eye S.jpg' AS image_url UNION ALL
    SELECT 'Dell UltraSharp WB7022' AS product_name, '/image/Dell UltraSharp WB7022.jpg' AS image_url UNION ALL
    SELECT 'Elgato Facecam MK.2' AS product_name, '/image/Elgato Facecam MK.2.jpg' AS image_url UNION ALL
    SELECT 'Elgato Facecam Pro' AS product_name, '/image/Elgato Facecam Pro.jpg' AS image_url UNION ALL
    SELECT 'Logitech Brio 4K' AS product_name, '/image/Logitech Brio 4K.jpg' AS image_url UNION ALL
    SELECT 'Logitech C920s HD Pro' AS product_name, '/image/Logitech C920s HD Pro.jpg' AS image_url UNION ALL
    SELECT 'Logitech StreamCam' AS product_name, '/image/Logitech StreamCam.jpg' AS image_url UNION ALL
    SELECT 'Razer Kiyo Pro' AS product_name, '/image/Razer Kiyo Pro.jpg' AS image_url UNION ALL
    SELECT 'Razer Kiyo X' AS product_name, '/image/Razer Kiyo X.jpg' AS image_url
) seed ON seed.product_name = p.product_name
SET i.image_url = seed.image_url
WHERE i.image_url = '/images/no-image.svg';
