/**
 * 
 */
 
 <script type="application/javascript">
//alert('inbcode');
  var myfield=document.getElementById('ginput_quantity_12_7');
  var soldoutSpan = document.createElement('span')
  soldoutSpan.innerHTML = '<b>SOLD OUT</b>';
  myfield.parentNode.appendChild(soldoutSpan);
  myfield.style.display="none";
  soldoutSpan.style.color="red";
</script>