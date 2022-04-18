using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces.interfaces
{
    internal interface IMove
    {
        string Move(String[] initMessages);
        Dictionary<string,string[]> Status();
        string GetName();
    }
}
